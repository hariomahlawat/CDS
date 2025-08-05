package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStat
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqAnalyticsScreen(
    nav: NavController,
    vm: PyqAnalyticsViewModel = hiltViewModel()
) {
    val window by vm.window.collectAsState()
    val stats by vm.stats.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("PYQ Analytics") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            FilterChipRow(window) { vm.setWindow(it) }

            Spacer(Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Text("Attempt a PYQ to unlock analytics.")
            } else {
                TopicBarList(stats)

                Spacer(Modifier.height(24.dp))

                val weak = vm.weakestTopic()
                if (weak != null) {
                    Button(onClick = {
                        nav.navigate("english/pyqp/revision?topic=${weak.topic}")
                    }) {
                        Text("Retake weakest topic (${weak.topic})")
                    }
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
        AnalyticsRepository.Window.values().forEach { w ->
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
private fun TopicBarList(stats: List<TopicStat>) {
    val max = stats.maxOf { it.percent }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.take(10).forEach { s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.topic, Modifier.width(120.dp))
                Canvas(Modifier.weight(1f).height(16.dp)) {
                    val frac = if (max == 0f) 0f else s.percent / max
                    drawRect(
                        color = MaterialTheme.colorScheme.primary,
                        size = Size(size.width * frac, size.height)
                    )
                }
                Spacer(Modifier.width(8.dp))
                val pct = "%.0f".format(s.percent)
                Text("$pct %")
            }
        }
    }
}
