package com.concepts_and_quizzes.cds.ui.reports.time

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.TimeAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/* ============================== Public entry =============================== */

@Composable
fun TimePage(
    window: Any? = null,
    onStartQuiz: () -> Unit = {},
    vm: TimeViewModel = hiltViewModel()
) {
    val days = remember(window) { resolveDays(window) }
    val ui by vm.state.collectAsState()

    LaunchedEffect(days) { vm.refresh(days) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Study time", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        TimeContent(ui = ui, onStartQuiz = onStartQuiz)
    }
}

/* Separate renderer to avoid calling Composables from non-Composable contexts */
@Composable
private fun TimeContent(ui: TimeUiState, onStartQuiz: () -> Unit) {
    when (ui) {
        is TimeUiState.Loading -> LoadingView()
        is TimeUiState.Empty -> EmptyTimeView(onStartQuiz)
        is TimeUiState.Error -> ErrorView(ui.message)
        is TimeUiState.Data -> DataView(ui)
    }
}

/* ========================= ViewModel, state & models ======================== */

@HiltViewModel
class TimeViewModel @Inject constructor(
    private val repo: TimeAnalysisRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TimeUiState>(TimeUiState.Loading)
    val state: StateFlow<TimeUiState> = _state.asStateFlow()

    fun refresh(days: Int) {
        viewModelScope.launch {
            val cutoff = cutoffForDays(days)

            val dailyFlow = repo.dailyMinutes(days)
                .map { list ->
                    list.asReversed().map { DailyMinutes(day = it.day, minutes = it.minutes) }
                }

            val summaryFlow = combine(
                repo.totalMinutesSince(cutoff),
                repo.activeDaysSince(cutoff),
                repo.bestDaySince(cutoff)
            ) { total, active, best ->
                TimeSummary(
                    totalMin = (total ?: 0.0).toInt(),
                    activeDays = active,
                    bestDay = best?.day,
                    bestMin = best?.minutes?.toInt() ?: 0
                )
            }

            val effFlow = repo.efficiencySince(cutoff)
                .map { e ->
                    Efficiency(
                        secPerQ = e?.secPerQ ?: 0.0,
                        accuracyPct = (e?.accuracy ?: 0.0) * 100.0
                    )
                }

            combine(dailyFlow, summaryFlow, effFlow) { daily, summary, eff ->
                if (daily.isEmpty()) TimeUiState.Empty
                else TimeUiState.Data(TimeUiData(daily), summary, eff)
            }
                .onStart { _state.value = TimeUiState.Loading }
                .catch { e -> _state.value = TimeUiState.Error(e.message ?: "Something went wrong") }
                .collect { _state.value = it }
        }
    }

    private fun cutoffForDays(days: Int): Long {
        if (days >= 36500) return 0L // lifetime
        val now = System.currentTimeMillis()
        return now - days.toLong() * 24L * 3600L * 1000L
    }
}

private fun resolveDays(window: Any?): Int = when (window?.toString()) {
    "D7" -> 7
    "D30" -> 30
    "LIFETIME" -> 36500
    else -> 7
}

/* ------------------------------- UI models --------------------------------- */

data class DailyMinutes(val day: String, val minutes: Double)

data class TimeUiData(val daily: List<DailyMinutes>)

data class TimeSummary(
    val totalMin: Int,
    val activeDays: Int,
    val bestDay: String?,
    val bestMin: Int
)

data class Efficiency(
    val secPerQ: Double,
    val accuracyPct: Double
)

/** Public so it can be used in a public property type without visibility clash. */
sealed class TimeUiState {
    data object Loading : TimeUiState()
    data object Empty : TimeUiState()
    data class Error(val message: String) : TimeUiState()
    data class Data(
        val data: TimeUiData,
        val summary: TimeSummary,
        val efficiency: Efficiency
    ) : TimeUiState()
}

/* ================================== UI ===================================== */

@Composable private fun LoadingView() {
    Text("Loading…", style = MaterialTheme.typography.bodyMedium)
}

@Composable private fun EmptyTimeView(onStartQuiz: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No time tracked yet", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Text("Complete a quiz to see your study time here.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onStartQuiz) { Text("Start a quiz") }
    }
}

@Composable private fun ErrorView(message: String) {
    Text("Failed to load: $message", color = MaterialTheme.colorScheme.error)
}

@Composable private fun DataView(state: TimeUiState.Data) {
    TimeHeader(state.summary)
    Spacer(Modifier.height(8.dp))

    val daily = state.data.daily
    if (daily.size <= 2) {
        CompactTimeTiles(daily)
    } else {
        DailyMinutesChart(data = daily, modifier = Modifier
            .fillMaxWidth()
            .height(220.dp))
    }

    Spacer(Modifier.height(12.dp))
    EfficiencyCard(state.efficiency)
}

@Composable private fun TimeHeader(summary: TimeSummary) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatTile("Total", "${summary.totalMin} min", Modifier.weight(1f))
        StatTile("Active days", "${summary.activeDays}", Modifier.weight(1f))
        val best = summary.bestDay?.let { monthDay(it) }?.let { "$it · ${summary.bestMin}m" } ?: "—"
        StatTile("Best day", best, Modifier.weight(1f))
    }
}

@Composable private fun EfficiencyCard(eff: Efficiency) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Efficiency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${eff.secPerQ.format1()} sec / Q", style = MaterialTheme.typography.titleMedium)
            }
            Column(Modifier.weight(1f)) {
                Text("Accuracy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${eff.accuracyPct.format0()}%", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable private fun StatTile(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/* ---------- NEW: compact tiles for 1–2 days instead of bars ---------- */

@Composable
private fun CompactTimeTiles(daily: List<DailyMinutes>) {
    val recent = if (daily.size <= 2) daily else daily.takeLast(2) // daily is oldest->latest
    val single = recent.size == 1

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (single) Arrangement.Center else Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        recent.forEach { d ->
            val label = labelForDay(d.day)
            val value = "${d.minutes.toInt()} min"
            val mod = if (single) Modifier.widthIn(max = 240.dp) else Modifier.weight(1f)
            StatTile(label, value, mod)
        }
    }
}

/* --------------------------- Chart (3+ days only) -------------------------- */

@Composable
private fun DailyMinutesChart(
    data: List<DailyMinutes>,
    modifier: Modifier = Modifier
) {
    val max = (data.maxOfOrNull { it.minutes } ?: 1.0).coerceAtLeast(1.0)
    val labelEvery = when {
        data.size <= 10 -> 1
        data.size <= 20 -> 2
        else -> 5
    }

    Column(modifier) {
        val barColor = MaterialTheme.colorScheme.primary
        val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
        Canvas(Modifier.fillMaxWidth().height(160.dp)) {
            val gridColor = gridColor
            val steps = 4
            val stepY = size.height / (steps + 1)
            repeat(steps) { i ->
                val y = size.height - stepY * (i + 1)
                drawLine(color = gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            }

            val gap = 6f
            val barCount = data.size.coerceAtLeast(1)
            val rawBarWidth = ((size.width - gap * (barCount + 1)) / barCount).coerceAtLeast(2f)
            val maxBarWidthPx = 24.dp.toPx()
            val barWidth = kotlin.math.min(rawBarWidth, maxBarWidthPx)
            val totalBarsWidth = barCount * barWidth + (barCount + 1) * gap
            val xOffset = ((size.width - totalBarsWidth).coerceAtLeast(0f)) / 2f

            val barColor = barColor
            data.forEachIndexed { i, d ->
                val h = if (max <= 0.0) 0f else ((d.minutes / max).toFloat() * size.height)
                val left = xOffset + gap + i * (barWidth + gap)
                val radius = minOf(barWidth / 6f, 8.dp.toPx(), h / 2f)
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, size.height - h),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }

            drawIntoCanvas { /* reserved for value hints later */ }
        }

        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            data.forEachIndexed { i, d ->
                val show = i % labelEvery == 0 || i == data.lastIndex
                Text(if (show) monthDay(d.day) else "", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Accessibility layer
        Row(Modifier.fillMaxWidth().height(0.dp)) {
            data.forEach { d ->
                val desc = "${d.minutes.toInt()} minutes on ${readableDate(d.day)}"
                Spacer(Modifier.weight(1f).semantics { contentDescription = desc })
            }
        }
    }
}

/* ================================ Utils ==================================== */

private fun monthDay(yyyyMmDd: String): String =
    if (yyyyMmDd.length >= 10) yyyyMmDd.substring(5, 10) else yyyyMmDd

private fun readableDate(yyyyMmDd: String): String {
    return if (yyyyMmDd.length >= 10) {
        val y = yyyyMmDd.substring(0, 4)
        val m = yyyyMmDd.substring(5, 7)
        val d = yyyyMmDd.substring(8, 10)
        "$d-$m-$y"
    } else yyyyMmDd
}

private fun labelForDay(yyyyMmDd: String): String {
    val today = todayIso()
    val yesterday = yesterdayIso()
    return when (yyyyMmDd) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> monthDay(yyyyMmDd)
    }
}

private fun todayIso(): String = calendarToIso(Calendar.getInstance())
private fun yesterdayIso(): String = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.let(::calendarToIso)

private fun calendarToIso(cal: Calendar): String {
    val y = cal.get(Calendar.YEAR)
    val m = (cal.get(Calendar.MONTH) + 1)
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m, d)
}

private fun Double.format0(): String = String.format(Locale.getDefault(), "%.0f", this)
private fun Double.format1(): String = String.format(Locale.getDefault(), "%.1f", this)
