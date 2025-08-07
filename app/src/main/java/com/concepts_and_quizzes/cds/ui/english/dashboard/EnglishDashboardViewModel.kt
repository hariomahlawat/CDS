package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.discover.DiscoverRepository
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EnglishDashboardViewModel @Inject constructor(
    private val progressDao: PyqpProgressDao,
    private val discoverRepo: DiscoverRepository
) : ViewModel() {
    data class PyqSummary(val papers: Int, val best: Int, val last: Int)

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

    val tips: StateFlow<List<ConceptEntity>> = discoverRepo.todaysTips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onBookmarkToggle(id: Int) = viewModelScope.launch { discoverRepo.toggleBookmark(id) }

    fun isBookmarked(id: Int) = discoverRepo.isBookmarked(id)
}
