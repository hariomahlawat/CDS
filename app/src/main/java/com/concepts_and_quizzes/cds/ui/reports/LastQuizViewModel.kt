package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.repo.LastReviewRepository
import com.concepts_and_quizzes.cds.data.analytics.repo.LastReviewResult
import com.concepts_and_quizzes.cds.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LastQuizViewModel @Inject constructor(
    private val repo: LastReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<LastUi>>(UiState.Loading)
    val state: StateFlow<UiState<LastUi>> = _state.asStateFlow()

    private var _lastSessionId: String? = null

    fun load(sessionId: String?) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                val sid = sessionId ?: repo.latestSessionId()
                _lastSessionId = sid
                if (sid == null) {
                    _state.value = UiState.Empty("No recent quiz", "Reload")
                    return@launch
                }

                val r: LastReviewResult = repo.load(sid)
                if (r.total == 0) {
                    _state.value = UiState.Empty("No recent quiz", "Reload")
                } else {
                    _state.value = UiState.Data(
                        LastUi(
                            total = r.total,
                            attempted = r.attempted,
                            correct = r.correct,
                            scoreOn100 = r.scoreOn100,
                            questions = r.questions
                        )
                    )
                }
            }.onFailure { e ->
                _state.value = UiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun refresh() = load(_lastSessionId)

    /* ------------------------------- UI model ------------------------------- */

    data class LastUi(
        val total: Int,
        val attempted: Int,
        val correct: Int,
        val scoreOn100: Int,
        val questions: List<com.concepts_and_quizzes.cds.data.analytics.repo.LastUiQuestion>
    )
}
