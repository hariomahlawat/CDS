package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class EnglishDashboardViewModel @Inject constructor(
    progressDao: PyqpProgressDao
) : ViewModel() {
    data class PyqSummary(val papers: Int, val best: Int, val last: Int)

    val summary: StateFlow<PyqSummary> = progressDao.getAll()
        .map { list ->
            val best = list.maxOfOrNull { if (it.attempted == 0) 0 else it.correct * 100 / it.attempted } ?: 0
            val last = list.lastOrNull()?.let { if (it.attempted == 0) 0 else it.correct * 100 / it.attempted } ?: 0
            PyqSummary(list.size, best, last)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PyqSummary(0,0,0))
}
