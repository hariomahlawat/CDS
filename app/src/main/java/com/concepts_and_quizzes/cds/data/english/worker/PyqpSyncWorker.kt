package com.concepts_and_quizzes.cds.data.english.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.concepts_and_quizzes.cds.core.util.sha256
import com.concepts_and_quizzes.cds.data.english.db.PyqpImportLog
import com.concepts_and_quizzes.cds.data.english.db.EnglishDatabase
import com.concepts_and_quizzes.cds.data.english.pyqp.PyqpJsonReader
import com.concepts_and_quizzes.cds.data.english.pyqp.toEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.coroutineScope

@HiltWorker
class PyqpSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val db: EnglishDatabase,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(appContext, params) {

    private object PrefKeys {
        val hashes = stringSetPreferencesKey("imported_pyqp_hashes")
    }

    override suspend fun doWork(): Result = coroutineScope {
        val assetPaths = applicationContext.assets.list("pyqp")?.map { "pyqp/$it" } ?: emptyList()
        if (assetPaths.isEmpty()) return@coroutineScope Result.success()

        val fileHashes = assetPaths.map { path ->
            val bytes = applicationContext.assets.open(path).readBytes()
            Triple(path, bytes.sha256(), bytes)
        }

        val allHashes = fileHashes.map { it.second }.toSet()
        val cached = dataStore.data.first()[PrefKeys.hashes] ?: emptySet()
        if (cached.containsAll(allHashes)) return@coroutineScope Result.success()

        val imported = db.pyqpImportLogDao().getAllHashes().toMutableSet()
        fileHashes.forEach { (path, hash, bytes) ->
            if (hash !in imported) {
                val (paperId, dtos) = PyqpJsonReader.parse(applicationContext, path)
                db.pyqpQuestionDao().insertAll(dtos.map { it.toEntity(paperId) })
                db.pyqpImportLogDao().insert(PyqpImportLog(hash, path))
                imported.add(hash)
            }
        }

        dataStore.edit { prefs ->
            prefs[PrefKeys.hashes] = imported
        }
        Result.success()
    }
}
