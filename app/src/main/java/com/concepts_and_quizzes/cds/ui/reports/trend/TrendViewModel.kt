package com.concepts_and_quizzes.cds.ui.reports.trend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TrendViewModel @Inject constructor(
    private val useCases: TrendUseCases
) : ViewModel() {

    private val windows = listOf("7D", "30D", "All")
    private val _selectedWindowIndex = MutableStateFlow(0)
    private val _windowArg = MutableStateFlow("D7")

    private val _state = MutableStateFlow<UiState<TrendUi>>(UiState.Loading)
    val state: StateFlow<UiState<TrendUi>> = _state

    init { refresh() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWindowArg(arg: String) {
        _windowArg.value = arg
        _selectedWindowIndex.value = when (arg) { "D7" -> 0; "D30" -> 1; else -> 2 }
        refresh()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onWindowIndexSelected(index: Int) {
        _selectedWindowIndex.value = index
        _windowArg.value = when (index) { 0 -> "D7"; 1 -> "D30"; else -> "LIFETIME" }
        refresh()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.stream(_windowArg.value, System.currentTimeMillis())
                .map { raw -> raw.toUi(windows, _selectedWindowIndex.value) }
                .catch { e -> emit(UiState.Error(e.message ?: "Something went wrong")) }
                .collect { _state.value = it }
        }
    }

    fun share() {
        // Hook share intent in the host screen if needed
    }
}

/* ------------------------------ Mapping ------------------------------ */

private fun TrendRaw.toUi(windows: List<String>, selectedWindowIndex: Int): UiState<TrendUi> {
    if (daily.isEmpty()) return UiState.Empty(title = "No data yet", actionLabel = "Start practice")

    val attempts = daily.sumOf { it.attempts }
    val correct = daily.sumOf { it.correct }
    val accuracyPct = if (attempts > 0) ((correct.toFloat() / attempts) * 100).roundToInt() else 0

    // Chart points (compact label MM-DD)
    val points = daily.map {
        TrendPointUI(
            label = it.day.substring(5),
            attempts = it.attempts,
            correct = it.correct,
            avgSecPerQ = avgSecPerQ
        )
    }

    // Topic momentum: pick top 6 by volume in current window
    val topTopics = dailyTopicsForSelection()
    val topics = topTopics.map { topicId ->
        val nowAcc = topicAccNow[topicId] ?: 0
        val prevAcc = topicAccPrev[topicId] ?: nowAcc
        TopicMomentumUI(
            topic = topicId,                     // swap with pretty name mapper when ready
            accuracyPct = nowAcc,
            deltaPct = (nowAcc - prevAcc)
        )
    }

    val insights = buildList {
        add(InsightUI("Accuracy $accuracyPct% in this window"))
        bestDayLabel?.let { add(InsightUI("Best day: $it")) }
        if (topics.isNotEmpty()) {
            val worst = topics.minByOrNull { it.accuracyPct }
            worst?.let { add(InsightUI("Focus on ${it.topic}: ${it.accuracyPct}% accuracy", actionLabel = "Revise", actionRoute = "english/pyqp?mode=WRONGS&topic=${it.topic}")) }
        }
    }

    val ui = TrendUi(
        windowLabel = when (windowArg) { "D7" -> "7D"; "D30" -> "30D"; else -> "All" },
        windows = windows,
        selectedWindowIndex = selectedWindowIndex,
        kpis = TrendKpiUI(
            attempts = attempts,
            accuracyPct = accuracyPct,
            avgSecPerQ = avgSecPerQ,
            bestDayLabel = bestDayLabel
        ),
        points = points,
        topics = topics,
        insights = insights
    )
    return UiState.Data(ui)
}

/**
 * Heuristic: choose top 6 topicIds by current-window volume using the already-aggregated daily rows.
 * If you have per-topic totals handy, replace this with that map for accuracy.
 */
private fun TrendRaw.dailyTopicsForSelection(): List<String> {
    // Approximation: if TopicTrendPointDb not exposed here, we only know accuracy maps.
    // We fallback to picking topics by presence in 'topicAccNow'. Caller may adjust later.
    return topicAccNow.keys.take(6).toList()
}
