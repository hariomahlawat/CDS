package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.settings.UserPreferences
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
    private val _report = MutableStateFlow<QuizReport?>(null)
    val report: StateFlow<QuizReport?> = _report

    fun load(sessionId: String?) {
        viewModelScope.launch {
            val sid = sessionId ?: repo.latestSessionId() ?: return@launch
            _report.value = repo.analyse(sid)
        }
    }
}

