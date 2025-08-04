package com.concepts_and_quizzes.cds.ui.english.concepts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.english.repo.EnglishRepository
import com.concepts_and_quizzes.cds.domain.english.EnglishTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ConceptDetailViewModel @Inject constructor(
    repo: EnglishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val topicId: String = savedStateHandle.get<String>("id")!!

    val topic: StateFlow<EnglishTopic?> = repo.getTopic(topicId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
