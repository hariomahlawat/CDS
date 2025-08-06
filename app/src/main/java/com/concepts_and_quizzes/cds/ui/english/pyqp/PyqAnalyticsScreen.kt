package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStat
import com.concepts_and_quizzes.cds.data.analytics.db.TrendPoint
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqAnalyticsScreen(
    nav: NavController,
    vm: PyqAnalyticsViewModel = hiltViewModel()
) {
    val window by vm.window.collectAsState()
    val stats by vm.stats.collectAsState()
    val trend by vm.trend.collectAsState()
    val tab by vm.tab.collectAsState()
    var highContrast by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("PYQ Analytics") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            FilterChipRow(window) { vm.setWindow(it) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("High contrast")
                Spacer(Modifier.width(8.dp))
                Switch(checked = highContrast, onCheckedChange = { highContrast = it })
            }

            Spacer(Modifier.height(16.dp))

            TabRow(selectedTabIndex = tab.ordinal) {
                Tab(
                    text = { Text("Overview") },
                    selected = tab == PyqAnalyticsViewModel.Tab.OVERVIEW,
                    onClick = { vm.setTab(PyqAnalyticsViewModel.Tab.OVERVIEW) }
                )
                Tab(
                    text = { Text("Trend") },
                    selected = tab == PyqAnalyticsViewModel.Tab.TREND,
                    onClick = { vm.setTab(PyqAnalyticsViewModel.Tab.TREND) }
                )
            }

            Spacer(Modifier.height(16.dp))

            when (tab) {
                PyqAnalyticsViewModel.Tab.OVERVIEW -> {
                    if (stats.isEmpty()) {
                        Text("Attempt a PYQ to unlock analytics.")
                    } else {
                        TopicBarList(stats, highContrast)

                        Spacer(Modifier.height(24.dp))

                        val weak = vm.weakestTopic()
                        if (weak != null) {
                            Button(onClick = {
                                nav.navigate("english/pyqp?mode=WRONGS&topic=${weak.topic}")
                            }) {
                                Text("Retake weakest topic (${weak.topic})")
                            }
                        }
                    }
                }

                PyqAnalyticsViewModel.Tab.TREND -> {
                    TrendTab(trend, highContrast)
                }
            }
        }
    }
}

@Composable
private fun FilterChipRow(
    selected: AnalyticsRepository.Window,
    onSelect: (AnalyticsRepository.Window) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AnalyticsRepository.Window.entries.forEach { w ->
            FilterChip(
                selected = w == selected,
                onClick = { onSelect(w) },
                label = {
                    Text(
                        when (w) {
                            AnalyticsRepository.Window.LAST_7 -> "7 days"
                            AnalyticsRepository.Window.LAST_30 -> "30 days"
                            AnalyticsRepository.Window.LIFETIME -> "All time"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun TopicBarList(stats: List<TopicStat>, highContrast: Boolean) {
    val max = stats.maxOf { it.percent }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.take(10).forEach { s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.topic, Modifier.width(120.dp))
                val pct = "%.0f".format(s.percent)
                val container = MaterialTheme.colorScheme.tertiaryContainer
                val content = contentColorFor(container)
                Canvas(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .semantics { contentDescription = "${s.topic} $pct percent correct" }
                ) {
                    val frac = if (max == 0f) 0f else s.percent / max
                    val barWidth = size.width * frac
                    if (highContrast) {
                        val barColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        val stripeColor = MaterialTheme.colorScheme.onSurface
                        drawRect(color = barColor, size = Size(barWidth, size.height))
                        val step = 8.dp.toPx()
                        var x = -size.height
                        while (x < barWidth) {
                            drawLine(
                                color = stripeColor,
                                start = Offset(x, 0f),
                                end = Offset(x + size.height, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                            x += step
                        }
                    } else {
                        drawRect(color = container, size = Size(barWidth, size.height))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text("$pct %", color = content)
            }
        }
    }
}

@Composable
private fun TrendTab(points: List<TrendPoint>, highContrast: Boolean) {
    if (points.isEmpty()) {
        Text("Attempt at least one paper to see your trend.", Modifier.padding(24.dp))
        return
    }

    SparkLineChart(points, highContrast)
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        points.forEach {
            val pct = "%.0f".format(it.percent)
            Text(pct, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SparkLineChart(points: List<TrendPoint>, highContrast: Boolean) {
    val max = points.maxOf { it.percent }
    val anim = remember { Animatable(0f) }
    val context = LocalContext.current
    val animationsDisabled = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    LaunchedEffect(points, animationsDisabled) {
        if (animationsDisabled) {
            anim.snapTo(1f)
        } else {
            anim.animateTo(1f, tween(600))
        }
    }

    val desc = points.joinToString {
        val pct = "%.0f".format(it.percent)
        val week = Instant.ofEpochMilli(it.weekStart).atZone(ZoneId.systemDefault()).toLocalDate()
        "$week : $pct percent"
    }

    val baseColor = if (highContrast) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary
    val effect = if (highContrast) PathEffect.dashPathEffect(floatArrayOf(10f, 10f)) else null

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp)
            .semantics { contentDescription = desc }
    ) {
        val stepX = if (points.size == 1) 0f else size.width / (points.size - 1)
        val path = Path()
        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = size.height * (1 - p.percent / max.coerceAtLeast(1f))
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        clipRect(right = size.width * anim.value) {
            drawPath(
                path = path,
                color = baseColor,
                style = Stroke(width = 4.dp.toPx(), pathEffect = effect)
            )
        }
    }
}
