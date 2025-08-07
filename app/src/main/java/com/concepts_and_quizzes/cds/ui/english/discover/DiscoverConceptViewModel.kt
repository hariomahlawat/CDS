package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.discover.DiscoverRepository
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DiscoverConceptViewModel @Inject constructor(
    private val repo: DiscoverRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle["id"])

    private val _concept = MutableStateFlow<ConceptEntity?>(null)
    val concept: StateFlow<ConceptEntity?> = _concept

    val bookmarked: StateFlow<Boolean> = repo.isBookmarked(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch { _concept.value = repo.getConcept(id) }
    }

    fun toggleBookmark() = viewModelScope.launch { repo.toggleBookmark(id) }
}
