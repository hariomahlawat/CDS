package com.concepts_and_quizzes.cds.data.analytics

import javax.inject.Inject
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.SessionQuestionMapDao
import com.concepts_and_quizzes.cds.data.english.db.PyqpDao

/**
 * Repository that prepares review items combining attempts, questions and mappings.
 */
class QuizReviewRepository @Inject constructor(
    private val attemptDao: AttemptLogDao,
    private val mapDao: SessionQuestionMapDao,
    private val questionDao: PyqpDao
) {
    suspend fun reviewForSession(sessionId: String): List<ReviewItem> {
        val attempts = attemptDao.forSession(sessionId)
        val maps = mapDao.forSession(sessionId).sortedBy { it.questionIndex }
        val qids = maps.map { it.questionId }.distinct()
        val questions = questionDao.getQuestionsByIds(qids)
        val byId = questions.associateBy { it.qid }
        return maps.mapNotNull { m ->
            val q = byId[m.questionId] ?: return@mapNotNull null
            val attempt = attempts.firstOrNull { it.questionIndex == m.questionIndex }
            val opts = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
            ReviewItem(
                index = m.questionIndex,
                questionId = m.questionId,
                question = q.question,
                options = opts,
                correctIndex = q.correctIndex,
                selectedIndex = attempt?.selectedIndex
            )
        }
    }
}

/** UI model for quiz review. */
data class ReviewItem(
    val index: Int,
    val questionId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val selectedIndex: Int?
)
