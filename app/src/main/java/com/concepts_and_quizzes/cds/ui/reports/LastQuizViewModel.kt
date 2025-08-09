package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.QuizReviewRepository
import com.concepts_and_quizzes.cds.data.analytics.ReviewItem
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.analytics.scoring.MarkingScheme
import com.concepts_and_quizzes.cds.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@HiltViewModel
class LastQuizViewModel @Inject constructor(
    private val reportRepo: QuizReportRepository,
    private val reviewRepo: QuizReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<LastUi>>(UiState.Loading)
    val state: StateFlow<UiState<LastUi>> = _state.asStateFlow()

    private var _lastSessionId: String? = null

    fun load(sessionId: String?) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                val sid = sessionId ?: reportRepo.latestSessionId()
                _lastSessionId = sid
                if (sid == null) {
                    _state.value = UiState.Empty("No recent quiz", "Reload")
                    return@launch
                }

                val report: QuizReport = reportRepo.analyse(sid)
                val items: List<ReviewItem> = reviewRepo.reviewForSession(sid)

                _state.value = UiState.Data(toUi(report, items))
            }.onFailure { e ->
                _state.value = UiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun refresh() = load(_lastSessionId)

    /* ---------------------------- mapping ---------------------------- */

    private fun toUi(report: QuizReport, items: List<ReviewItem>): LastUi {
        // Normalize to 100 using active marking scheme (CDS: +1, -1/3, 0)
        val perCorrect = MarkingScheme.CDS.markPerCorrect
        val maxMarks = (report.total * perCorrect).takeIf { it > 0f } ?: 1f
        val raw = report.score ?: 0f
        val scoreOn100 = ((raw / maxMarks) * 100f).coerceIn(0f, 100f).roundToInt()

        val uiQs = items.map { mapReviewItemToUi(it) }
        return LastUi(
            total = report.total,
            attempted = report.attempted,
            correct = report.correct,
            scoreOn100 = scoreOn100,
            questions = uiQs
        )
    }

    private fun mapReviewItemToUi(item: ReviewItem): LastUiQuestion {
        // item.options is the authoritative list of option strings
        val optionModels = item.options.mapIndexed { idx, text ->
            LastUiOption(
                text = text,
                isCorrect = (idx == item.correctIndex),
                isSelected = (item.selectedIndex != null && idx == item.selectedIndex)
            )
        }
        return LastUiQuestion(
            questionId = item.questionId.toString(), // keep key stable for Lazy list
            text = item.question,
            options = optionModels
        )
        // No optionA/B/C/D calls; those don’t exist in ReviewItem.
    }

    // ------------------------------- UI models -------------------------------

    data class LastUi(
        val total: Int,
        val attempted: Int,
        val correct: Int,
        val scoreOn100: Int,
        val questions: List<LastUiQuestion>
    )

    data class LastUiQuestion(
        val questionId: String,
        val text: String,
        val options: List<LastUiOption>
    )

    data class LastUiOption(
        val text: String,
        val isCorrect: Boolean,
        val isSelected: Boolean
    )
}
