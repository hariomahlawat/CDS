package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.LastReviewDao
import com.concepts_and_quizzes.cds.data.analytics.db.PyqpQuestionRow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class LastReviewRepository @Inject constructor(
    private val dao: LastReviewDao
) {

    suspend fun latestSessionId(): String? {
        return dao.lastCompletedSessionFromSessions()
            ?: dao.lastSessionFromAttempts()
    }

    suspend fun load(sessionId: String): LastReviewResult {
        val map = dao.mappedQuestions(sessionId)
        val selections = dao.latestSelections(sessionId)

        // If map exists use that order; else fall back to "attempted" questions
        val qids: List<String> = if (map.isNotEmpty()) map.map { it.qid } else selections.map { it.qid }
        if (qids.isEmpty()) return LastReviewResult.empty()

        val bank        = dao.pyqpQuestionsByIds(qids)
        val bankById    = bank.associateBy { it.qid }
        val selById     = selections.associateBy { it.qid }
        val orderedQids = if (map.isNotEmpty()) map.sortedBy { it.ordinal }.map { it.qid } else qids

        val questions = orderedQids.mapNotNull { qid ->
            val q = bankById[qid] ?: return@mapNotNull null
            val selIdx = selById[qid]?.selectedIndex
            toUiQuestion(q, selIdx)
        }

        val total     = if (map.isNotEmpty()) map.size else questions.size
        val attempted = selections.count { it.selectedIndex != null }
        val correct   = questions.count { q -> q.options.any { it.isCorrect && it.isSelected } }
        val scoreOn100 = normalizeCdsTo100(correct, attempted, total)

        return LastReviewResult(total, attempted, correct, scoreOn100, questions)
    }

    private fun toUiQuestion(row: PyqpQuestionRow, selectedIndex: Int?): LastUiQuestion {
        val opts = listOf(row.optionA, row.optionB, row.optionC, row.optionD)
        val optionModels = opts.mapIndexed { idx, text ->
            LastUiOption(
                text = text,
                isCorrect = (idx == row.correctIndex),
                isSelected = (selectedIndex != null && idx == selectedIndex)
            )
        }
        return LastUiQuestion(
            questionId = row.qid,
            text = row.stem,
            options = optionModels
        )
    }

    /** CDS: +1 correct, −1/3 wrong, 0 unattempted → normalize to /100 */
    private fun normalizeCdsTo100(correct: Int, attempted: Int, total: Int): Int {
        if (total <= 0) return 0
        val wrong = (attempted - correct).coerceAtLeast(0)
        val raw   = correct * 1.0 - wrong * (1.0 / 3.0)
        val max   = total * 1.0
        val pct   = ((raw / max) * 100.0).coerceIn(0.0, 100.0)
        return pct.roundToInt()
    }
}

/* ---------------------------- domain models ---------------------------- */

data class LastReviewResult(
    val total: Int,
    val attempted: Int,
    val correct: Int,
    val scoreOn100: Int,
    val questions: List<LastUiQuestion>
) {
    companion object { fun empty() = LastReviewResult(0, 0, 0, 0, emptyList()) }
}

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
