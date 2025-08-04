package com.concepts_and_quizzes.cds

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.data.english.db.SeedUtil
import com.concepts_and_quizzes.cds.data.english.worker.PyqpSyncWorker

@HiltAndroidApp
class CdsApplication : Application(), Configuration.Provider {
    @Inject lateinit var seedUtil: SeedUtil
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob()).launch { seedUtil.seedIfEmpty() }
        WorkManager.getInstance(this).enqueueUniqueWork(
            "pyqp-sync",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<PyqpSyncWorker>().build()
        )
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}
