package com.concepts_and_quizzes.cds.data.english.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.WorkerFactory
import androidx.work.ListenableWorker
import androidx.room.Room
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
class PyqpSyncWorkerTest {
    private lateinit var context: Context
    private lateinit var db: EnglishDatabase
    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, EnglishDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("test_pyqp")
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun createWorker(): PyqpSyncWorker {
        return TestListenableWorkerBuilder<PyqpSyncWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    params: WorkerParameters
                ): ListenableWorker? {
                    return PyqpSyncWorker(appContext, params, db, dataStore)
                }
            })
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    @Test
    fun importsJsonOnlyOnce() = runBlocking {
        val worker1 = createWorker()
        worker1.doWork()
        val firstCount = db.pyqpQuestionDao().count()
        assert(firstCount > 0)
        assertEquals(1, db.pyqpImportLogDao().getAllHashes().size)

        val worker2 = createWorker()
        worker2.doWork()
        assertEquals(firstCount, db.pyqpQuestionDao().count())
        assertEquals(1, db.pyqpImportLogDao().getAllHashes().size)
    }
}
