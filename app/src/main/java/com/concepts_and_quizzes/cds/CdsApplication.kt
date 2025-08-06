package com.concepts_and_quizzes.cds

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.concepts_and_quizzes.cds.data.english.db.SeedUtil

@HiltAndroidApp
class CdsApplication : Application() {
    @Inject lateinit var seedUtil: SeedUtil

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            seedUtil.seedIfEmpty()
        }
    }
}
