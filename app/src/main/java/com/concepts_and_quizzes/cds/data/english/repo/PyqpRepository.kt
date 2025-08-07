package com.concepts_and_quizzes.cds.data.english.repo

import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.english.db.PyqpDao
import com.concepts_and_quizzes.cds.domain.english.AnswerOption
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PyqpRepository @Inject constructor(
    private val dao: PyqpDao,
    private val attemptDao: AttemptLogDao
) {
    fun getPaperList(): Flow<List<PyqpPaper>> =
        dao.getDistinctPaperIds().map { ids ->
            ids.map { PyqpPaper(it, extractYear(it)) }
        }

    fun getQuestions(paperId: String): Flow<List<PyqpQuestion>> =
        dao.getQuestionsByPaper(paperId).map { list ->
            list.map { e ->
                PyqpQuestion(
                    id = e.qid,
                    text = e.question,
                    options = listOf(
                        AnswerOption(e.optionA, e.correctIndex == 0),
                        AnswerOption(e.optionB, e.correctIndex == 1),
                        AnswerOption(e.optionC, e.correctIndex == 2),
                        AnswerOption(e.optionD, e.correctIndex == 3)
                    ).shuffled(),
                    direction = e.direction,
                    passage = e.passageText,
                    passageTitle = e.passageTitle,
                    topic = e.topic
                )
            }
        }

    suspend fun wrongOnlyQuestions(topicId: String): List<PyqpQuestion> {
        val qids = attemptDao.latestWrongQids(topicId)
        if (qids.isEmpty()) return emptyList()
        return dao.getQuestionsByIds(qids).map { e ->
            PyqpQuestion(
                id = e.qid,
                text = e.question,
                options = listOf(
                    AnswerOption(e.optionA, e.correctIndex == 0),
                    AnswerOption(e.optionB, e.correctIndex == 1),
                    AnswerOption(e.optionC, e.correctIndex == 2),
                    AnswerOption(e.optionD, e.correctIndex == 3)
                ).shuffled(),
                direction = e.direction,
                passage = e.passageText,
                passageTitle = e.passageTitle,
                topic = e.topic
            )
        }
    }
}

data class PyqpPaper(val id: String, val year: Int)

private fun extractYear(id: String): Int =
    Regex("(\\d{4})").find(id)?.value?.toInt() ?: 0
