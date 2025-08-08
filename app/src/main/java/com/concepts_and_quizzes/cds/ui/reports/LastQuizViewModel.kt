package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.settings.UserPreferences
import com.concepts_and_quizzes.cds.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LastQuizViewModel @Inject constructor(
    private val repo: QuizReportRepository,
    val prefs: UserPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<QuizReport>>(UiState.Loading)
    val state: StateFlow<UiState<QuizReport>> = _state

    private var sessionId: String? = null

    fun load(sessionId: String?) {
        this.sessionId = sessionId
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val sid = sessionId ?: repo.latestSessionId()
                if (sid == null) {
                    _state.value = UiState.Empty("No reports", "Reload")
                    return@launch
                }
                val report = repo.analyse(sid)
                _state.value = UiState.Data(report)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Failed to load report")
            }
        }
    }
}

