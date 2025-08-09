package com.concepts_and_quizzes.cds.ui.reports.heatmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.db.DailyHeatDb
import com.concepts_and_quizzes.cds.data.analytics.db.HourlyHeatDb
import com.concepts_and_quizzes.cds.data.analytics.repo.HeatmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/* ============================== Public entry =============================== */

@Composable
fun HeatmapPage(
    window: Any? = null,
    vm: HeatmapViewModel = hiltViewModel()
) {
    val days = remember(window) { resolveDays(window) }
    val ui by vm.state.collectAsState()
    LaunchedEffect(days) { vm.refresh(days) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Heatmap", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        ModeTabs(vm)

        Spacer(Modifier.height(12.dp))
        when (val s = ui) {
            is HeatmapUi.Loading -> Text("Loading…", style = MaterialTheme.typography.bodyMedium)
            is HeatmapUi.Error -> Text("Failed to load: ${s.msg}", color = MaterialTheme.colorScheme.error)
            is HeatmapUi.Empty -> EmptyView()
            is HeatmapUi.Daily -> {
                DailyHeatmapCard(s)
                Spacer(Modifier.height(8.dp))
                Legend(s.maxMinutes)
            }
            is HeatmapUi.Hourly -> {
                HourlyHeatmapCard(s)
                Spacer(Modifier.height(8.dp))
                Legend(s.maxMinutes)
            }
        }
    }
}

/* ========================= ViewModel, state & models ======================== */

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val repo: HeatmapRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(Mode.Days) // Days or Hours
    val mode: StateFlow<Mode> = _mode.asStateFlow()

    private val _state = MutableStateFlow<HeatmapUi>(HeatmapUi.Loading)
    val state: StateFlow<HeatmapUi> = _state.asStateFlow()

    fun switch(mode: Mode) { _mode.value = mode }

    fun refresh(days: Int) {
        viewModelScope.launch {
            val cutoff = cutoffForDays(days)

            mode.flatMapLatest { m ->
                when (m) {
                    Mode.Days -> repo.dailySince(cutoff).map { toDailyUi(it, desiredWeeks(days)) }
                    Mode.Hours -> repo.hourlySince(cutoff).map { toHourlyUi(it) }
                }
            }
                .onStart { _state.value = HeatmapUi.Loading }
                .catch { e -> _state.value = HeatmapUi.Error(e.message ?: "Error") }
                .collect { ui -> _state.value = ui }
        }
    }

    private fun cutoffForDays(days: Int): Long {
        if (days >= 36500) return 0L
        val now = System.currentTimeMillis()
        return now - days.toLong() * 24L * 3600L * 1000L
    }
}

/* Modes and state */

enum class Mode { Days, Hours }

sealed class HeatmapUi {
    data object Loading : HeatmapUi()
    data object Empty : HeatmapUi()
    data class Error(val msg: String) : HeatmapUi()
    data class Daily(
        val matrix: List<List<Double>>, // rows 0..6 (Mon..Sun), cols weeks left->right
        val weekKeys: List<Int>,         // YYYYWW for columns
        val maxMinutes: Double
    ) : HeatmapUi()
    data class Hourly(
        val matrix: List<List<Double>>, // rows 0..6 (Mon..Sun), cols 0..23 hours
        val maxMinutes: Double
    ) : HeatmapUi()
}

/* =============================== UI chrome ================================ */

@Composable
private fun ModeTabs(vm: HeatmapViewModel) {
    val mode by vm.mode.collectAsState()
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(text = "By day", selected = mode == Mode.Days) { vm.switch(Mode.Days) }
        FilterChip(text = "By hour", selected = mode == Mode.Hours) { vm.switch(Mode.Hours) }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        onClick = onClick,
        shape = shape,
        color = bg,
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Text(text, color = fg, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}

/* =============================== Daily card ================================ */

@Composable
private fun DailyHeatmapCard(data: HeatmapUi.Daily) {
    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Study days", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            DailyHeatmapGrid(
                matrix = data.matrix,
                maxMinutes = data.maxMinutes
            )
            Spacer(Modifier.height(6.dp))
            WeekdayLabelsRow()
        }
    }
}

/* GitHub-style calendar: rows Mon..Sun, columns weeks left->right */
@Composable
private fun DailyHeatmapGrid(
    matrix: List<List<Double>>,
    maxMinutes: Double
) {
    val rows = 7
    val cols = matrix.firstOrNull()?.size ?: 0
    if (cols == 0) return

    BoxWithConstraints(Modifier.fillMaxWidth().heightIn(min = 120.dp)) {
        val gap = 2.dp
        val maxTile = 16.dp
        val tile = min(
            maxTile,
            ((maxWidth - gap * (cols + 1)) / cols.toFloat()).coerceAtLeast(8.dp)
        )
        val tilePx = tile.toPx()
        val gapPx = gap.toPx()

        val levels = remember { colorSteps(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceVariant) }

        Canvas(Modifier.fillMaxWidth().height(rows * tile + (rows + 1) * gap)) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val minutes = matrix[r][c]
                    val x = gapPx + c * (tilePx + gapPx)
                    val y = gapPx + r * (tilePx + gapPx)
                    val color = if (minutes <= 0.0 || maxMinutes <= 0.0) levels[0]
                    else levels[levelFor(minutes / maxMinutes, levels.size)]
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(tilePx, tilePx),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
        }

        // a11y overlay
        Row(Modifier.fillMaxWidth().height(rows * tile + (rows + 1) * gap)) {
            repeat(cols) { c ->
                Column(Modifier.width(tile).padding(horizontal = gap/2)) {
                    repeat(rows) { r ->
                        val minutes = matrix[r][c].toInt()
                        val desc = "${weekdayName(r)} · $minutes min"
                        Spacer(Modifier
                            .height(tile)
                            .fillMaxWidth()
                            .semantics { contentDescription = desc })
                        Spacer(Modifier.height(gap))
                    }
                }
                Spacer(Modifier.width(gap))
            }
        }
    }
}

/* ============================== Hourly card ================================ */

@Composable
private fun HourlyHeatmapCard(data: HeatmapUi.Hourly) {
    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text("By hour of day", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            HourlyHeatmapGrid(matrix = data.matrix, maxMinutes = data.maxMinutes)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("00", style = MaterialTheme.typography.labelSmall)
                Text("12", style = MaterialTheme.typography.labelSmall)
                Text("23", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(6.dp))
            WeekdayLabelsRow()
        }
    }
}

/* rows Mon..Sun, cols 0..23 hours */
@Composable
private fun HourlyHeatmapGrid(
    matrix: List<List<Double>>,
    maxMinutes: Double
) {
    val rows = 7
    val cols = 24
    if (matrix.isEmpty()) return

    BoxWithConstraints(Modifier.fillMaxWidth().heightIn(min = 160.dp)) {
        val gap = 2.dp
        val tile = min(18.dp, ((maxWidth - gap * (cols + 1)) / cols.toFloat()).coerceAtLeast(8.dp))
        val tilePx = tile.toPx()
        val gapPx = gap.toPx()
        val levels = remember { colorSteps(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceVariant) }

        Canvas(Modifier.fillMaxWidth().height(rows * tile + (rows + 1) * gap)) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val minutes = matrix[r][c]
                    val x = gapPx + c * (tilePx + gapPx)
                    val y = gapPx + r * (tilePx + gapPx)
                    val color = if (minutes <= 0.0 || maxMinutes <= 0.0) levels[0]
                    else levels[levelFor(minutes / maxMinutes, levels.size)]
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(tilePx, tilePx),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }
        }

        // a11y overlay
        Row(Modifier.fillMaxWidth().height(rows * tile + (rows + 1) * gap)) {
            repeat(cols) { c ->
                Column(Modifier.width(tile).padding(horizontal = gap/2)) {
                    repeat(rows) { r ->
                        val minutes = matrix[r][c].toInt()
                        val desc = "${weekdayName(r)} · hour $c · $minutes min"
                        Spacer(Modifier
                            .height(tile)
                            .fillMaxWidth()
                            .semantics { contentDescription = desc })
                        Spacer(Modifier.height(gap))
                    }
                }
                Spacer(Modifier.width(gap))
            }
        }
    }
}

/* =============================== Legend & UX =============================== */

@Composable
private fun Legend(maxMinutes: Double) {
    val levels = remember { colorSteps(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceVariant) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Less", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        levels.forEach { c ->
            Box(Modifier.size(14.dp).background(c, shape = MaterialTheme.shapes.small))
        }
        val mid = (maxMinutes / 2.0).toInt()
        Text("More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text("${0}m", style = MaterialTheme.typography.labelSmall)
        Text("·", style = MaterialTheme.typography.labelSmall)
        Text("${mid}m", style = MaterialTheme.typography.labelSmall)
        Text("·", style = MaterialTheme.typography.labelSmall)
        Text("${maxMinutes.toInt()}m", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun WeekdayLabelsRow() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").forEach {
            Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Clip)
        }
    }
}

/* ================================ Mapping ================================== */

private fun toDailyUi(rows: List<DailyHeatDb>, desiredWeeks: Int): HeatmapUi {
    if (rows.isEmpty()) return HeatmapUi.Empty
    // Build week list left->right, capped to desired weeks
    val byWeek = rows.groupBy { it.yw }.toSortedMap()
    val weekKeys = byWeek.keys.takeLast(desiredWeeks).toList()
    val cols = weekKeys.size
    if (cols == 0) return HeatmapUi.Empty

    // matrix rows Mon..Sun, not Sun..Sat → remap dow 0..6 to 0..6 with Mon=0
    fun monRow(dow: Int): Int = if (dow == 0) 6 else dow - 1

    val matrix = MutableList(7) { MutableList(cols) { 0.0 } }
    var maxVal = 0.0
    weekKeys.forEachIndexed { c, wk ->
        byWeek[wk]?.forEach { d ->
            val r = monRow(d.dow)
            matrix[r][c] = matrix[r][c] + d.minutes
            if (matrix[r][c] > maxVal) maxVal = matrix[r][c]
        }
    }
    return HeatmapUi.Daily(matrix = matrix.map { it.toList() }, weekKeys = weekKeys, maxMinutes = maxVal)
}

private fun toHourlyUi(rows: List<HourlyHeatDb>): HeatmapUi {
    if (rows.isEmpty()) return HeatmapUi.Empty
    fun monRow(dow: Int): Int = if (dow == 0) 6 else dow - 1
    val matrix = MutableList(7) { MutableList(24) { 0.0 } }
    var maxVal = 0.0
    rows.forEach { r ->
        val rr = monRow(r.dow)
        matrix[rr][r.hour] = r.minutes
        if (r.minutes > maxVal) maxVal = r.minutes
    }
    return HeatmapUi.Hourly(matrix = matrix.map { it.toList() }, maxMinutes = maxVal)
}

private fun resolveDays(window: Any?): Int = when (window?.toString()) {
    "D7" -> 7
    "D30" -> 35           // 5 weeks for better grid
    "LIFETIME" -> 36500   // capped by desiredWeeks()
    else -> 35
}

private fun desiredWeeks(days: Int): Int {
    return when {
        days >= 36500 -> 52
        days <= 7 -> 1
        else -> ceil(days / 7.0).toInt().coerceAtLeast(4).coerceAtMost(12)
    }
}

/* ================================ Colours ================================== */

private fun colorSteps(primary: Color, base: Color): List<Color> {
    // 5 levels from base → tinted → primary. Works in light and dark.
    fun mix(a: Color, b: Color, t: Float): Color =
        Color(
            red = a.red + (b.red - a.red) * t,
            green = a.green + (b.green - a.green) * t,
            blue = a.blue + (b.blue - a.blue) * t,
            alpha = 1f
        )
    return listOf(
        base.copy(alpha = 0.35f),
        mix(base, primary, 0.35f),
        mix(base, primary, 0.55f),
        mix(base, primary, 0.75f),
        mix(base, primary, 0.95f),
    )
}

private fun levelFor(norm: Double, steps: Int): Int {
    if (norm <= 0.0) return 0
    val s = steps - 1
    val idx = (norm * s).toInt().coerceIn(1, s)
    return idx
}

/* ================================ Helpers ================================== */

private fun weekdayName(rowMon0: Int): String =
    listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")[rowMon0]

/* Unused but handy if you ever need an ISO date for today/yesterday */
private fun todayIso(): String = calendarToIso(Calendar.getInstance())
private fun calendarToIso(cal: Calendar): String {
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m, d)
}
