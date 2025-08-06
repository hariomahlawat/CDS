package com.concepts_and_quizzes.cds.data.quiz

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.quizDataStore by preferencesDataStore(name = "quiz_resume")

@Singleton
class QuizResumeStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class Store(val paperId: String, val snapshot: String)

    private val PAPER_ID = stringPreferencesKey("paper_id")
    private val SNAPSHOT = stringPreferencesKey("snapshot")

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    init {
        scope.launch {
            context.quizDataStore.data.map { prefs ->
                val id = prefs[PAPER_ID]
                val snap = prefs[SNAPSHOT]
                if (id != null && snap != null) Store(id, snap) else null
            }.collect { _store.value = it }
        }
    }

    suspend fun save(
        paperId: String,
        answers: Map<Int, Int>,
        flags: Set<Int>,
        pageIndex: Int,
        remaining: Int,
        durations: Map<Int, Int> = emptyMap(),
    ) {
        val ans = answers.entries.joinToString(";") { "${it.key}:${it.value}" }
        val flg = flags.joinToString(",")
        val dur = durations.entries.joinToString(";") { "${it.key}:${it.value}" }
        val snapshot = listOf(pageIndex, ans, flg, remaining, dur).joinToString("|")
        context.quizDataStore.edit { prefs ->
            prefs[PAPER_ID] = paperId
            prefs[SNAPSHOT] = snapshot
        }
    }

    suspend fun restore(snapshot: String) {
        context.quizDataStore.edit { it[SNAPSHOT] = snapshot }
    }

    suspend fun clear() {
        context.quizDataStore.edit { it.clear() }
    }
}
