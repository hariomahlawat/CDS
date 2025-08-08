package com.concepts_and_quizzes.cds.ui.english.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.availability.ModeAvailability
import com.concepts_and_quizzes.cds.data.analytics.availability.ModeAvailabilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.data.quiz.QuizResumeStore

@HiltViewModel
class QuizHubViewModel @Inject constructor(
    private val resumeStore: QuizResumeStore,
    private val availabilityRepo: ModeAvailabilityRepository
) : ViewModel() {
    val store: StateFlow<QuizResumeStore.Store?> = resumeStore.store

    private val _availability = MutableStateFlow<ModeAvailability?>(null)
    val availability: StateFlow<ModeAvailability?> = _availability

    data class SavedProgress(
        val paperId: String,
        val questionIndex: Int,
        val percent: Int
    )

    private fun parseProgress(store: QuizResumeStore.Store): SavedProgress {
        val parts = store.snapshot.split("|")
        val pageIndex = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val answered = parts.getOrNull(1)?.takeIf { it.isNotBlank() }?.split(";")?.size ?: 0
        val percent = answered * 100 / 60
        return SavedProgress(store.paperId, pageIndex, percent)
    }

    val progress: StateFlow<SavedProgress?> =
        resumeStore.store.map { it?.let(::parseProgress) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { _availability.value = availabilityRepo.fetch() }
    }

    fun restore(snapshot: String) {
        viewModelScope.launch { resumeStore.restore(snapshot) }
    }
}
