package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PyqpDaoTest {
    private lateinit var db: EnglishDatabase
    private lateinit var seedUtil: SeedUtil

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seedUtil = SeedUtil(context, db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun seedInsertsPaper() = runBlocking {
        seedUtil.seedIfEmpty()
        val ids = db.pyqpDao().getDistinctPaperIds().first()
        assertEquals(listOf("CDS_II_2024_English_SetA.json"), ids)
    }
}
