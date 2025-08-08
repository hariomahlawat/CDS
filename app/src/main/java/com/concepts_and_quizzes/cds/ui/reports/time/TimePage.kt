package com.concepts_and_quizzes.cds.ui.reports.time

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.TimeAnalysisRepository
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.skeleton.TimeSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

/** UI model holding time analysis data. */
data class DailyMinutes(val day: String, val minutes: Double)

data class TimeUiData(val daily: List<DailyMinutes>)

@HiltViewModel
class TimeViewModel @Inject constructor(
    private val repo: TimeAnalysisRepository
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<TimeUiData>>(UiState.Loading)
    val state: StateFlow<UiState<TimeUiData>> = _state

    init { refresh() }

    fun refresh() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                val daily = repo.dailyMinutes(7).first().map { DailyMinutes(it.day, it.minutes) }
                if (daily.isEmpty()) {
                    UiState.Empty("No time data", "Reload")
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
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.TIME,
        unlocked = false,
        progress = 0f,
        reason = LockedReason.TimeGate(5.hours)
    ),
    vm: TimeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    GhostOverlay(
        status = status,
        skeleton = { TimeSkeleton() },
    ) {
        when (val s = state) {
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh() }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
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
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val barSpacing = size.width / (data.size * 2f)
        data.forEachIndexed { index, item ->
            val x = barSpacing * (index * 2 + 1)
            val barHeight = (item.minutes / max * size.height).toFloat()
            drawLine(
                color = MaterialTheme.colorScheme.primary,
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barSpacing,
                cap = StrokeCap.Round
            )
        }
    }
}

