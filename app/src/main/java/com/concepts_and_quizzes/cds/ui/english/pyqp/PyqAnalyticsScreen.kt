package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStat
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.ui.analytics.TrendTab
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqAnalyticsScreen(
    nav: NavController,
    vm: PyqAnalyticsViewModel = hiltViewModel()
) {
    val stats by vm.stats.collectAsState()
    val trend by vm.trend.collectAsState()
    val tab by vm.tab.collectAsState()
    var highContrast by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("PYQ Analytics") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            FilterChipRow(vm)

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
private fun FilterChipRow(vm: PyqAnalyticsViewModel) {
    val selected by vm.window.collectAsState()
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AnalyticsRepository.Window.entries.forEach { w ->
            FilterChip(
                selected = w == selected,
                onClick = { vm.setWindow(w) },
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
                val barColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                val stripeColor = MaterialTheme.colorScheme.onSurface
                Canvas(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .semantics { contentDescription = "${s.topic} $pct percent correct" }
                ) {
                    val frac = if (max == 0f) 0f else s.percent / max
                    val barWidth = size.width * frac
                    if (highContrast) {

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
