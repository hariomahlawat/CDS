package com.concepts_and_quizzes.cds.exam

import androidx.lifecycle.ViewModel
import com.concepts_and_quizzes.cds.data.local.entities.QuestionWithDirectionAndPassage
import com.concepts_and_quizzes.cds.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val repository: ExamRepository
) : ViewModel() {

    fun loadExamQuestions(examId: String): Flow<List<QuestionWithDirectionAndPassage>> =
        repository.getQuestionsWithDetails(examId)
}
