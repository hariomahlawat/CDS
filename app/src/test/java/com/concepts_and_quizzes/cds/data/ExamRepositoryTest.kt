package com.concepts_and_quizzes.cds.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.local.AppDatabase
import com.concepts_and_quizzes.cds.data.local.entities.DirectionEntity
import com.concepts_and_quizzes.cds.data.local.entities.ExamEntity
import com.concepts_and_quizzes.cds.data.local.entities.PassageEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionEntity
import com.concepts_and_quizzes.cds.data.repository.ExamRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExamRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ExamRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = ExamRepository(db.examDao(), db.directionDao(), db.passageDao(), db.questionDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveQuestionsWithMultipleDirections() = runBlocking {
        val exam = ExamEntity(
            examId = "CDS-II-2024-ENGLISH",
            year = 2024,
            session = "CDS-II",
            subject = "English",
            totalQuestions = 2,
            maxMarks = 100,
            examDate = "2024-09-01"
        )
        val directions = listOf(
            DirectionEntity("CDS-II-2024-ENGLISH-D1", exam.examId, "Grammar", "Direction 1"),
            DirectionEntity("CDS-II-2024-ENGLISH-D2", exam.examId, "Grammar", "Direction 2")
        )
        val passages = emptyList<PassageEntity>()
        val questions = listOf(
            QuestionEntity(
                examId = exam.examId,
                questionNumber = 1,
                question = "Q1?",
                optionA = "A",
                optionB = "B",
                optionC = "C",
                optionD = "D",
                correctAnswer = "A",
                topic = null,
                subTopic = null,
                difficulty = null,
                remarks = null,
                passageId = null,
                directionId = directions[0].directionId
            ),
            QuestionEntity(
                examId = exam.examId,
                questionNumber = 2,
                question = "Q2?",
                optionA = "A",
                optionB = "B",
                optionC = "C",
                optionD = "D",
                correctAnswer = "B",
                topic = null,
                subTopic = null,
                difficulty = null,
                remarks = null,
                passageId = null,
                directionId = directions[1].directionId
            )
        )
        repository.insertFullExam(exam, directions, passages, questions)
        val result = repository.getQuestionsWithDetails(exam.examId).first()
        assertEquals(2, result.size)
        assertEquals("Direction 1", result[0].direction?.text)
        assertEquals("Direction 2", result[1].direction?.text)
    }
}
