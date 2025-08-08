package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Window(val label: String) { D7("7D"), D30("30D"), LIFETIME("All") }

@HiltViewModel
class ReportsSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val startPage: Int = savedStateHandle.get<String>("startPage")?.toIntOrNull() ?: 0

    private val _window = MutableStateFlow(Window.D7)
    val window: StateFlow<Window> = _window.asStateFlow()

    fun setWindow(window: Window) {
        _window.value = window
    }
}
