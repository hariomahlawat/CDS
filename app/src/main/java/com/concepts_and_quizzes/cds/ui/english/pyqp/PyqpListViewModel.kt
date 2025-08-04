package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.data.english.repo.PyqpPaper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PyqpListViewModel @Inject constructor(
    repo: PyqpRepository
) : ViewModel() {
    val papers: StateFlow<List<PyqpPaper>> = repo.getPaperList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
