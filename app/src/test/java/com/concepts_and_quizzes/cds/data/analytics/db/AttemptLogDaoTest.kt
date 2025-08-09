package com.concepts_and_quizzes.cds.data.analytics.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
class AttemptLogDaoTest {

    private lateinit var db: EnglishDatabase
    private lateinit var dao: AttemptLogDao

    @Before
    fun setUp() {
        // Use a timezone with a non-zero offset to surface UTC/local mismatches
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"))
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.attemptLogDao()

        // Insert a question required for join in getTrend()
        runBlocking {
            db.questionDao().insertAll(
                listOf(
                    EnglishQuestionEntity(
                        qid = "Q1",
                        topicId = "T1",
                        question = "q",
                        optionA = "a",
                        optionB = "b",
                        optionC = "c",
                        optionD = "d",
                        correct = "A"
                    )
                )
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getTrend_usesLocalDate() = runTest {
        val ts = LocalDate.of(2025, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        dao.insertAll(
            listOf(
                AttemptLogEntity(
                    qid = "Q1",
                    quizId = "quiz",
                    correct = true,
                    flagged = false,
                    durationMs = 0,
                    timestamp = ts
                )
            )
        )

        val trend = dao.getTrend(0L).first()
        assertEquals(1, trend.size)
        assertEquals("2025-01-01", trend.first().day)
    }
}

