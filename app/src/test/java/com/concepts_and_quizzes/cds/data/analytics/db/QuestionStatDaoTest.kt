package com.concepts_and_quizzes.cds.data.analytics.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuestionStatDaoTest {
    private lateinit var db: EnglishDatabase
    private lateinit var dao: QuestionStatDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.questionStatDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertAndQueryWorks() = runTest {
        dao.upsert("Q1", true)
        dao.upsert("Q1", false)
        dao.upsert("Q2", false)
        dao.upsert("Q2", false)

        val count = dao.countWrong()
        assertEquals(2, count)
        val qids = dao.wrongQids(10)
        assertEquals(setOf("Q1", "Q2"), qids.toSet())
    }
}
