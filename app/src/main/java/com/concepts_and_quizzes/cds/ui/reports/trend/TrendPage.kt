@file:Suppress("unused") // until the screen is wired in the Reports tab

package com.concepts_and_quizzes.cds.ui.reports.trend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.skeleton.TrendSkeleton
import kotlin.math.abs
import kotlin.math.max

/* ------------------------- Public entry ------------------------- */
/** `window` comes from ReportsScreen: "D7" | "D30" | "LIFETIME" */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrendPage(
    window: Any? = null,
    /** Pass your real status if you gate this tab, or leave null to render directly. */
    status: ModuleStatus? = null,
    vm: TrendViewModel = hiltViewModel()
) {
    LaunchedEffect(window) { vm.setWindowArg(window as? String ?: "D7") }
    val state by vm.state.collectAsState()

    val content: @Composable () -> Unit = {
        when (val s = state) {
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh() }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
            is UiState.Data -> {
                val ui = s.value
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TrendHeader(
                            windowLabel = ui.windowLabel,
                            onShare = { vm.share() }
                        )
                    }
                    item { TrendKpisRow(ui.kpis) }
                    item { AccuracyVolumeChart(ui.points) }

                    if (ui.topics.isNotEmpty()) {
                        item { Text("By topic", style = MaterialTheme.typography.titleMedium) }
                        item { TopicMomentumGrid(ui.topics) }
                    }

                    if (ui.insights.isNotEmpty()) {
                        item { Text("Insights", style = MaterialTheme.typography.titleMedium) }
                        item { InsightsPanel(ui.insights) }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (status == null) content()
    else GhostOverlay(status = status, skeleton = { TrendSkeleton() }) { content() }
}

/* ------------------------- UI widgets (pure UI) ------------------------- */

@Composable
private fun TrendHeader(
    windowLabel: String,
    onShare: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Trend", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        AssistChip(onClick = onShare, label = { Text("Share") })
        Spacer(Modifier.width(8.dp))
        // Window is controlled by the Reports app bar; we just display the current label here.
        AssistChip(onClick = { /* controlled by app bar */ }, label = { Text(windowLabel) })
    }
}

@Composable
private fun TrendKpisRow(kpi: TrendKpiUI) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard("Attempts", kpi.attempts.toString(), Modifier.weight(1f))
            KpiCard("Accuracy", "${kpi.accuracyPct}%", Modifier.weight(1f))
            KpiCard("Avg sec/Q", kpi.avgSecPerQ.toString(), Modifier.weight(1f))
        }
        kpi.bestDayLabel?.let {
            Spacer(Modifier.height(6.dp))
            Text("Best day: $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AccuracyVolumeChart(points: List<TrendPointUI>, modifier: Modifier = Modifier) {
    val attemptsMax = max(1, points.maxOfOrNull { it.attempts } ?: 1)
    val accValues = points.map { if (it.attempts > 0) it.correct.toFloat() / it.attempts else 0f }
    val barColor = MaterialTheme.colorScheme.primary
    val lineColor = MaterialTheme.colorScheme.tertiary
    val track = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier) {
        Box(Modifier.fillMaxWidth().height(180.dp)) {
            Canvas(Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val padL = 12f
                val padR = 6f
                val padT = 10f
                val padB = 20f
                val chartW = w - padL - padR
                val chartH = h - padT - padB
                val n = points.size
                if (n == 0) return@Canvas

                // Bars
                val slot = chartW / n
                val barW = max(6f, slot * 0.5f)
                points.forEachIndexed { i, p ->
                    val x = padL + slot * i + (slot - barW) / 2f
                    val barH = if (attemptsMax == 0) 0f else (p.attempts / attemptsMax.toFloat()) * chartH
                    drawRect(color = track, topLeft = Offset(x, padT + (chartH - barH)), size = Size(barW, barH))
                    drawRect(color = barColor, topLeft = Offset(x, padT + (chartH - barH)), size = Size(barW, barH))
                }

                // Accuracy line
                val path = Path()
                accValues.forEachIndexed { i, acc ->
                    val x = padL + slot * i + slot / 2f
                    val y = padT + (1f - acc) * chartH
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path = path, color = lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (points.isNotEmpty()) {
                points.firstOrNull()?.let { Text(it.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                points.getOrNull(points.lastIndex / 2)?.let { Text(it.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                points.lastOrNull()?.let { Text(it.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun TopicMomentumGrid(topics: List<TopicMomentumUI>) {
    val rows = topics.chunked(2)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { t -> TopicCard(t, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TopicCard(t: TopicMomentumUI, modifier: Modifier = Modifier) {
    val deltaColor = when {
        t.deltaPct > 0 -> MaterialTheme.colorScheme.primary
        t.deltaPct < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(t.topic, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${t.accuracyPct}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Surface(shape = CircleShape, color = deltaColor.copy(alpha = 0.1f)) {
                    val arrow = if (t.deltaPct > 0) "▲" else if (t.deltaPct < 0) "▼" else "•"
                    Text(
                        text = "$arrow ${abs(t.deltaPct)}%",
                        color = deltaColor,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsPanel(insights: List<InsightUI>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        insights.forEach { ins ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ins.text, modifier = Modifier.weight(1f))
                    ins.actionLabel?.let { label ->
                        AssistChip(onClick = { /* route in host with ins.actionRoute */ }, label = { Text(label) })
                    }
                }
            }
        }
    }
}
