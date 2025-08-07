package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.CdsApplication
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

@HiltViewModel
class EnglishDashboardViewModel @Inject constructor(
    private val progressDao: PyqpProgressDao
) : ViewModel() {
    data class PyqSummary(val papers: Int, val best: Int, val last: Int)

    data class Concept(val id: String, val title: String, val overview: String)

    val summary: StateFlow<PyqSummary> = progressDao.getAll()
        .map { list ->
            val best = list.maxOfOrNull { if (it.attempted == 0) 0 else it.correct * 100 / it.attempted } ?: 0
            val last = list.lastOrNull()?.let { if (it.attempted == 0) 0 else it.correct * 100 / it.attempted } ?: 0
            PyqSummary(list.size, best, last)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PyqSummary(0,0,0))

    val questionsToday: StateFlow<Int> = progressDao.getAll()
        .map { list -> list.sumOf { it.attempted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _concepts = MutableStateFlow<List<Concept>>(emptyList())
    val concepts: StateFlow<List<Concept>> = _concepts

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _concepts.value = loadConcepts()
        }
    }

    private fun loadConcepts(): List<Concept> = runCatching {
        val json = CdsApplication.instance.assets
            .open("concept_of_the_day.json")
            .bufferedReader()
            .use { it.readText() }
        val array = JSONObject(json).getJSONArray("concepts")
        List(array.length()) { idx ->
            val obj = array.getJSONObject(idx)
            Concept(
                id = obj.optString("id"),
                title = obj.optString("title"),
                overview = obj.optString("overview")
            )
        }
    }.getOrDefault(emptyList())
}
