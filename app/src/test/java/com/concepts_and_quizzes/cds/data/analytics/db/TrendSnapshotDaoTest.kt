package com.concepts_and_quizzes.cds.data.analytics.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
class TrendSnapshotDaoTest {
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
    fun trendSnapshot_limitsAndOrders() = runTest {
        val base = LocalDate.of(2025, 1, 6) // Monday
        val attempts = mutableListOf<AttemptLogEntity>()
        for (w in 0 until 12) {
            val ts = base.plusWeeks(w.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            attempts += AttemptLogEntity(qid = "Q${w}a", quizId = "CDS$w", correct = true, flagged = false, durationMs = 0, timestamp = ts)
            attempts += AttemptLogEntity(qid = "Q${w}b", quizId = "CDS$w", correct = false, flagged = false, durationMs = 0, timestamp = ts + 1)
        }
        db.attemptLogDao().insertAll(attempts)

        val points = dao.trendSnapshot(0L).first()
        assertEquals(10, points.size)
        for (i in 1 until points.size) {
            assertTrue(points[i - 1].weekStart > points[i].weekStart)
        }
        assertEquals(50f, points.first().percent)
    }
}
