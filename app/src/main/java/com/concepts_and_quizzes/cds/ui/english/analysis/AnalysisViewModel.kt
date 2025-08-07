package com.concepts_and_quizzes.cds.ui.english.analysis

import androidx.lifecycle.SavedStateHandle
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
class AnalysisViewModel @Inject constructor(
    private val repo: QuizReportRepository,
    val prefs: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])
    private val _report = MutableStateFlow<QuizReport?>(null)
    val report: StateFlow<QuizReport?> = _report

    init {
        viewModelScope.launch {
            _report.value = repo.analyse(sessionId)
        }
    }
}
