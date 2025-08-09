package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.LastReviewDao
import com.concepts_and_quizzes.cds.data.analytics.db.MappedQuestionRow
import com.concepts_and_quizzes.cds.data.analytics.db.PyqpQuestionRow
import com.concepts_and_quizzes.cds.data.analytics.db.SelectionRowDb
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
        val qids: List<Long> = if (map.isNotEmpty()) map.map { it.qid } else selections.map { it.qid }

        if (qids.isEmpty()) return LastReviewResult.empty()

        val bank = dao.pyqpQuestionsByIds(qids)
        val bankById = bank.associateBy { it.qid }
        val selById = selections.associateBy { it.qid }

        // Build UI questions in the quiz order (if available), else by selection order
        val orderedQids = if (map.isNotEmpty()) map.sortedBy { it.ordinal }.map { it.qid } else qids

        val questions = orderedQids.mapNotNull { qid ->
            val q = bankById[qid] ?: return@mapNotNull null
            val sel = selById[qid]?.selectedIndex
            toUiQuestion(q, sel)
        }

        // Header metrics
        val total = if (map.isNotEmpty()) map.size else questions.size
        val attempted = selections.count { it.selectedIndex != null }
        val correct = questions.count { q -> q.options.any { it.isCorrect && it.isSelected } }

        val scoreOn100 = normalizeCdsTo100(correct = correct, attempted = attempted, total = total)

        return LastReviewResult(
            total = total,
            attempted = attempted,
            correct = correct,
            scoreOn100 = scoreOn100,
            questions = questions
        )
    }

    /* ---------------------------- helpers ---------------------------- */

    private fun toUiQuestion(row: PyqpQuestionRow, selectedIndex: Int?): LastUiQuestion {
        val opts = listOf(row.optionA, row.optionB, row.optionC, row.optionD).map { it ?: "" }
        val optionModels = opts.mapIndexed { idx, text ->
            LastUiOption(
                text = text,
                isCorrect = idx == row.correctIndex,
                isSelected = (selectedIndex != null && idx == selectedIndex)
            )
        }
        return LastUiQuestion(
            questionId = row.qid.toString(),
            text = row.stem,
            options = optionModels
        )
    }

    /**
     * CDS scheme: +1 per correct, −1/3 per wrong, 0 unattempted.
     * Normalized to /100 using max positive marks = total * 1.
     */
    private fun normalizeCdsTo100(correct: Int, attempted: Int, total: Int): Int {
        if (total <= 0) return 0
        val wrong = (attempted - correct).coerceAtLeast(0)
        val raw = correct * 1.0 - wrong * (1.0 / 3.0)
        val max = total * 1.0
        val pct = ((raw / max) * 100.0).coerceIn(0.0, 100.0)
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
    companion object {
        fun empty() = LastReviewResult(0, 0, 0, 0, emptyList())
    }
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
