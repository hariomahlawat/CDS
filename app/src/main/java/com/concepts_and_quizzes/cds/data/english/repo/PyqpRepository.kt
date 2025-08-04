package com.concepts_and_quizzes.cds.data.english.repo

import com.concepts_and_quizzes.cds.data.english.db.PyqpDao
import com.concepts_and_quizzes.cds.data.english.model.toDomain
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PyqpRepository @Inject constructor(
    private val dao: PyqpDao
) {
    fun getPaperList(): Flow<List<PyqpPaper>> =
        dao.getDistinctPaperIds().map { ids ->
            ids.map { PyqpPaper(it, extractYear(it)) }
        }

    fun getQuestions(paperId: String): Flow<List<PyqpQuestion>> =
        dao.getQuestionsByPaper(paperId).map { list -> list.map { it.toDomain() } }
}

data class PyqpPaper(val id: String, val year: Int)

private fun extractYear(id: String): Int =
    Regex("(\\d{4})").find(id)?.value?.toInt() ?: 0
