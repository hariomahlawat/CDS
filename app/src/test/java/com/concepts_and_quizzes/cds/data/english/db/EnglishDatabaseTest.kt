package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EnglishDatabaseTest {

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
    fun seedInsertsTopics() = runBlocking {
        seedUtil.seedIfEmpty()
        assertEquals(2, db.topicDao().count())
    }
}
