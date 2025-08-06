package com.concepts_and_quizzes.cds.data.analytics.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AttemptLogDaoTest {
    private lateinit var db: EnglishDatabase
    private lateinit var dao: AttemptLogDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.attemptLogDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun latestWrongQids_returnsLatestWrongAttempts() = runTest {
        val questions = listOf(
            PyqpQuestionEntity("Q1", "P", "q1", "a", "b", "c", "d", 0, topic = "Grammar"),
            PyqpQuestionEntity("Q2", "P", "q2", "a", "b", "c", "d", 1, topic = "Grammar"),
            PyqpQuestionEntity("Q3", "P", "q3", "a", "b", "c", "d", 2, topic = "Grammar")
        )
        db.pyqpDao().insertAll(questions)
        val now = System.currentTimeMillis()
        dao.insertAll(
            listOf(
                AttemptLogEntity(qid = "Q1", quizId = "Quiz1", correct = false, flagged = false, durationMs = 100, timestamp = now),
                AttemptLogEntity(qid = "Q1", quizId = "Quiz2", correct = true, flagged = false, durationMs = 100, timestamp = now + 1000),
                AttemptLogEntity(qid = "Q2", quizId = "Quiz1", correct = false, flagged = false, durationMs = 100, timestamp = now + 500),
                AttemptLogEntity(qid = "Q3", quizId = "Quiz1", correct = true, flagged = false, durationMs = 100, timestamp = now + 700)
            )
        )
        val result = dao.latestWrongQids("Grammar")
        assertEquals(listOf("Q2"), result)
    }
}

