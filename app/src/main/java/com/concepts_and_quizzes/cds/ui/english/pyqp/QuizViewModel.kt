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

    init {
        viewModelScope.launch {
            repo.getQuestions(paperId).collect { qs ->
                questions = qs
                if (qs.isNotEmpty()) {
                    index = 0
                    if (!showIntroIfNeeded()) {
                        _ui.value = QuizUi.Question(0, qs[0], null)
                    }
                }
            }
        }
    }

    fun select(idx: Int) {
        answers[index] = idx
        val q = questions[index]
        _ui.value = QuizUi.Question(index, q, idx)
    }

    fun next() {
        if (index < questions.lastIndex) {
            index++
            if (!showIntroIfNeeded()) {
                val q = questions[index]
                val sel = answers[index]
                _ui.value = QuizUi.Question(index, q, sel)
            }
        } else {
            val correct = answers.count { (i, ans) -> questions[i].correct == ans }
            _ui.value = QuizUi.Result(correct, questions.size)
        }
    }

    fun continueFromIntro() {
        val q = questions[index]
        val sel = answers[index]
        _ui.value = QuizUi.Question(index, q, sel)
    }

    private fun showIntroIfNeeded(): Boolean {
        val q = questions[index]
        val prev = questions.getOrNull(index - 1)
        val dir = q.direction.takeIf { it != null && it != prev?.direction }
        val passageText = q.passage.takeIf { it != null && it != prev?.passage }
        val passageTitle = q.passageTitle.takeIf { passageText != null }
        return if (dir != null || passageText != null) {
            _ui.value = QuizUi.SectionIntro(dir, passageTitle, passageText)
            true
        } else {
            false
        }
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
        data class SectionIntro(val direction: String?, val passageTitle: String?, val passage: String?) : QuizUi()
        data class Question(val index: Int, val question: PyqpQuestion, val userAnswerIndex: Int?) : QuizUi()
        data class Result(val correct: Int, val total: Int) : QuizUi()
    }
}
