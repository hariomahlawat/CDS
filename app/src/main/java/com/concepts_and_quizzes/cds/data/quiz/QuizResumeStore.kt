package com.concepts_and_quizzes.cds.data.quiz

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
class QuizResumeStore @Inject constructor() {
    data class Store(val paperId: String, val snapshot: String)

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    fun save(
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
        _store.value = Store(paperId, snapshot)
    }

    fun restore(snapshot: String) {
        _store.value = _store.value?.copy(snapshot = snapshot)
    }

    fun clear() {
        _store.value = null
    }
}
