package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: PyqpRepository,
    private val progressDao: PyqpProgressDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val paperId: String = savedStateHandle["paperId"]!!

    private val _ui = MutableStateFlow<QuizUi>(QuizUi.Loading)
    val ui: StateFlow<QuizUi> = _ui

    private var index = 0
    private var questions: List<PyqpQuestion> = emptyList()
    private val answers = mutableMapOf<Int, Int>()
    private val flags = mutableSetOf<Int>()

    val questionCount: Int get() = questions.size
    fun questionAt(i: Int) = questions[i]
    fun answerFor(i: Int): Int? = answers[i]
    fun isFlagged(i: Int) = flags.contains(i)

    init {
        viewModelScope.launch {
            repo.getQuestions(paperId).collect { qs ->
                questions = qs
                if (qs.isNotEmpty()) {
                    index = 0
                    emitQuestion()
                }
            }
        }
    }

    private fun emitQuestion() {
        val q = questions[index]
        val sel = answers[index]
        _ui.value = QuizUi.Question(index, questions.size, q, sel, flags.contains(index))
    }

    fun select(idx: Int) {
        answers[index] = idx
        emitQuestion()
    }

    fun next() {
        if (index < questions.lastIndex) {
            index++
            emitQuestion()
        } else {
            submit()
        }
    }

    fun prev() {
        if (index > 0) {
            index--
            emitQuestion()
        }
    }

    fun goTo(i: Int) {
        index = i.coerceIn(0, questions.lastIndex)
        emitQuestion()
    }

    fun toggleFlag() {
        if (!flags.add(index)) flags.remove(index)
        emitQuestion()
    }

    fun submit() {
        val correct = answers.count { (i, ans) -> questions[i].correct == ans }
        _ui.value = QuizUi.Result(correct, questions.size)
    }

    fun saveProgress() {
        val correct = answers.count { (i, ans) -> questions[i].correct == ans }
        viewModelScope.launch {
            progressDao.upsert(
                PyqpProgress(paperId = paperId, correct = correct, attempted = questions.size)
            )
        }
    }

    sealed class QuizUi {
        object Loading : QuizUi()
        data class Question(
            val index: Int,
            val total: Int,
            val question: PyqpQuestion,
            val userAnswerIndex: Int?,
            val flagged: Boolean
        ) : QuizUi()
        data class Result(val correct: Int, val total: Int) : QuizUi()
    }
}
