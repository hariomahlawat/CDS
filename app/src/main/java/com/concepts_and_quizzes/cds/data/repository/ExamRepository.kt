package com.concepts_and_quizzes.cds.data.repository

import com.concepts_and_quizzes.cds.data.local.dao.DirectionDao
import com.concepts_and_quizzes.cds.data.local.dao.ExamDao
import com.concepts_and_quizzes.cds.data.local.dao.PassageDao
import com.concepts_and_quizzes.cds.data.local.dao.QuestionDao
import com.concepts_and_quizzes.cds.data.local.entities.DirectionEntity
import com.concepts_and_quizzes.cds.data.local.entities.ExamEntity
import com.concepts_and_quizzes.cds.data.local.entities.PassageEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionWithDirectionAndPassage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExamRepository @Inject constructor(
    private val examDao: ExamDao,
    private val directionDao: DirectionDao,
    private val passageDao: PassageDao,
    private val questionDao: QuestionDao
) {
    suspend fun insertFullExam(
        exam: ExamEntity,
        directions: List<DirectionEntity>,
        passages: List<PassageEntity>,
        questions: List<QuestionEntity>
    ) {
        examDao.insertExam(exam)
        directionDao.insertDirections(directions)
        passageDao.insertPassages(passages)
        questionDao.insertQuestions(questions)
    }

    fun getQuestionsWithDetails(examId: String): Flow<List<QuestionWithDirectionAndPassage>> =
        questionDao.getQuestionsWithDetails(examId)
}
