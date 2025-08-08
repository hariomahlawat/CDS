package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.english.repo.PyqpPaper
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class PyqpListViewModel @Inject constructor(
    private val repo: PyqpRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<PyqpPaper>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PyqpPaper>>> = _state

    init {
        refresh()
    }

    fun refresh() {
        repo.getPaperList()
            .onStart { _state.value = UiState.Loading }
            .catch { _state.value = UiState.Error("Failed to load papers") }
            .onEach { list ->
                _state.value = if (list.isEmpty()) {
                    UiState.Empty("No papers", "Reload")
                } else {
                    UiState.Data(list)
                }
            }
            .launchIn(viewModelScope)
    }
}
