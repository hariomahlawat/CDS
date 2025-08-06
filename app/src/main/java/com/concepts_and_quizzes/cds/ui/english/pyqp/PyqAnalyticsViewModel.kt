package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStat
import com.concepts_and_quizzes.cds.data.analytics.db.TrendPoint
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

@HiltViewModel
class PyqAnalyticsViewModel @Inject constructor(
    private val repo: AnalyticsRepository
) : ViewModel() {

    private val _window = MutableStateFlow(AnalyticsRepository.Window.LAST_30)
    val window: StateFlow<AnalyticsRepository.Window> = _window

    private val _tab = MutableStateFlow(Tab.OVERVIEW)
    val tab: StateFlow<Tab> = _tab

    val stats: StateFlow<List<TopicStat>> =
        _window.flatMapLatest { repo.topicSnapshot(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val trend: StateFlow<List<TrendPoint>> =
        _window.flatMapLatest { repo.trendSnapshot(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setWindow(w: AnalyticsRepository.Window) { _window.value = w }
    fun setTab(t: Tab) { _tab.value = t }
    fun weakestTopic(): TopicStat? = stats.value.minByOrNull { it.percent }

    enum class Tab { OVERVIEW, TREND }
}
