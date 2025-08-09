package com.concepts_and_quizzes.cds.ui.reports.time

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.TimeAnalysisRepository
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.reports.Window
import com.concepts_and_quizzes.cds.ui.skeleton.TimeSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/** UI model holding time analysis data. */
data class DailyMinutes(val day: String, val minutes: Double)

data class TimeUiData(val daily: List<DailyMinutes>)

@HiltViewModel
class TimeViewModel @Inject constructor(
    private val repo: TimeAnalysisRepository
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<TimeUiData>>(UiState.Loading)
    val state: StateFlow<UiState<TimeUiData>> = _state

    fun refresh(window: Window) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                val days = when (window) {
                    Window.D7 -> 7
                    Window.D30 -> 30
                    Window.LIFETIME -> 36_500
                }
                val daily = repo.dailyMinutes(days).first().map { DailyMinutes(it.day, it.minutes) }
                if (daily.isEmpty()) {
                    UiState.Empty("No tracked time yet", "Start quiz")
                } else {
                    UiState.Data(TimeUiData(daily))
                }
            }.onSuccess { _state.value = it }
                .onFailure { e ->
                    _state.value = UiState.Error(e.message ?: "Failed to load time data")
                }
        }
    }
}

@Composable
fun TimePage(
    window: Window,
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.TIME,
        unlocked = true,
        progress = 1f,
    ),
    vm: TimeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(window) { vm.refresh(window) }
    GhostOverlay(
        status = status,
        skeleton = { TimeSkeleton() },
    ) {
        when (val s = state) {
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh(window) }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh(window) }
            is UiState.Data -> TimeContent(s.value)
        }
    }
}

@Composable
private fun TimeContent(data: TimeUiData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.daily.isNotEmpty()) {
            Text("Study minutes", modifier = Modifier.align(Alignment.Start))
            DailyMinutesChart(data.daily)
        } else {
            Text("No time data")
        }
    }
}

@Composable
private fun DailyMinutesChart(data: List<DailyMinutes>) {
    val max = data.maxOfOrNull { it.minutes } ?: 1.0
    val dateParser = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val labelFormatter = remember { DateTimeFormatter.ofPattern("MM-dd") }
    val descFormatter = remember { DateTimeFormatter.ofPattern("EEE, d MMM") }
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val step = size.height / 4f
                repeat(3) { i ->
                    val y = size.height - step * (i + 1)
                    drawLine(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    val ratio = (item.minutes / max).toFloat()
                    val date = LocalDate.parse(item.day, dateParser)
                    val descDate = date.format(descFormatter)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(ratio)
                            .background(MaterialTheme.colorScheme.primary)
                            .semantics { contentDescription = "${item.minutes.roundToInt()} minutes on $descDate" }
                    )
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                val date = LocalDate.parse(item.day, dateParser)
                Text(date.format(labelFormatter), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

