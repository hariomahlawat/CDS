package com.concepts_and_quizzes.cds.data.analytics.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopicStatDaoTest {
    private lateinit var db: EnglishDatabase
    private lateinit var dao: TopicStatDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.topicStatDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun topicSnapshot_countsCorrectly() = runTest {
        val questions = listOf(
            PyqpQuestionEntity("Q1", "Paper", "q1", "a", "b", "c", "d", 0, topic = "Grammar", subTopic = "X"),
            PyqpQuestionEntity("Q2", "Paper", "q2", "a", "b", "c", "d", 1, topic = "Grammar", subTopic = "X"),
            PyqpQuestionEntity("Q3", "Paper", "q3", "a", "b", "c", "d", 2, topic = "Vocab", subTopic = "Y")
        )
        db.pyqpDao().insertAll(questions)
        val now = System.currentTimeMillis()
        db.attemptLogDao().insertAll(
            listOf(
                AttemptLogEntity(qid = "Q1", quizId = "Paper", correct = true, flagged = false, durationMs = 1000, timestamp = now),
                AttemptLogEntity(qid = "Q2", quizId = "Paper", correct = false, flagged = false, durationMs = 1000, timestamp = now),
                AttemptLogEntity(qid = "Q3", quizId = "Paper", correct = true, flagged = false, durationMs = 1000, timestamp = now)
            )
        )
        val stats = dao.topicSnapshot(0L).first()
        val grammar = stats.first { it.topic == "Grammar" }
        assertEquals(2, grammar.total)
        assertEquals(1, grammar.correct)
    }
}
