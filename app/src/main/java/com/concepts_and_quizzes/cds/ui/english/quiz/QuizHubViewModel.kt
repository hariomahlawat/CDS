package com.concepts_and_quizzes.cds.ui.english.quiz

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import com.concepts_and_quizzes.cds.data.quiz.QuizResumeStore

@HiltViewModel
class QuizHubViewModel @Inject constructor(
    private val resumeStore: QuizResumeStore
) : ViewModel() {
    val store: StateFlow<QuizResumeStore.Store?> = resumeStore.store

    fun restore(snapshot: String) {
        resumeStore.restore(snapshot)
    }
}
