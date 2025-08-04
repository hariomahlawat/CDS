package com.concepts_and_quizzes.cds.ui.english.concepts

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
class ConceptsHomeViewModel @Inject constructor(
    repo: EnglishRepository
) : ViewModel() {
    val topics: StateFlow<List<EnglishTopic>> = repo.getTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
