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

    private var pageIndex = 0
    private var pages: List<Item> = emptyList()
    private var questions: List<PyqpQuestion> = emptyList()
    private val answers = mutableMapOf<Int, Int>()
    private val flags = mutableSetOf<Int>()

    val pageCount: Int get() = pages.size
    val questionCount: Int get() = questions.size

    fun pageContent(i: Int): QuizPage {
        val item = pages[i]
        return when (item) {
            is Item.Intro -> QuizPage.Intro(item.direction, item.passageTitle, item.passage)
            is Item.Question -> QuizPage.Question(
                item.questionIndex,
                item.question,
                answers[item.questionIndex],
                flags.contains(item.questionIndex)
            )
        }
    }

    init {
        viewModelScope.launch {
            repo.getQuestions(paperId).collect { qs ->
                questions = qs
                if (qs.isNotEmpty()) {
                    buildPages(qs)
                    pageIndex = 0
                    emitPage()
                }
            }
        }
    }

    private fun buildPages(qs: List<PyqpQuestion>) {
        pages = buildList {
            var last: Section? = null
            qs.forEachIndexed { idx, q ->
                val sec = Section(q.direction, q.passage, q.passageTitle)
                if (sec != last && (q.direction != null || q.passage != null)) {
                    add(Item.Intro(q.direction, q.passageTitle, q.passage))
                }
                add(Item.Question(idx, q))
                last = sec
            }
        }
    }

    private fun emitPage() {
        val item = pages[pageIndex]
        when (item) {
            is Item.Intro -> _ui.value = QuizUi.Page(
                pageIndex,
                pages.size,
                questions.size,
                QuizPage.Intro(item.direction, item.passageTitle, item.passage)
            )
            is Item.Question -> _ui.value = QuizUi.Page(
                pageIndex,
                pages.size,
                questions.size,
                QuizPage.Question(
                    item.questionIndex,
                    item.question,
                    answers[item.questionIndex],
                    flags.contains(item.questionIndex)
                )
            )
        }
    }

    fun select(idx: Int) {
        val item = pages[pageIndex]
        if (item is Item.Question) {
            answers[item.questionIndex] = idx
            emitPage()
        }
    }

    fun next() {
        if (pageIndex < pages.lastIndex) {
            pageIndex++
            emitPage()
        } else {
            submit()
        }
    }

    fun prev() {
        if (pageIndex > 0) {
            pageIndex--
            emitPage()
        }
    }

    fun goTo(i: Int) {
        pageIndex = i.coerceIn(0, pages.lastIndex)
        emitPage()
    }

    fun toggleFlag() {
        val item = pages[pageIndex]
        if (item is Item.Question) {
            val qi = item.questionIndex
            if (!flags.add(qi)) flags.remove(qi)
            emitPage()
        }
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

    private data class Section(
        val direction: String?,
        val passage: String?,
        val passageTitle: String?
    )

    private sealed class Item {
        data class Intro(val direction: String?, val passageTitle: String?, val passage: String?) : Item()
        data class Question(val questionIndex: Int, val question: PyqpQuestion) : Item()
    }

    sealed class QuizUi {
        object Loading : QuizUi()
        data class Page(
            val pageIndex: Int,
            val pageCount: Int,
            val questionCount: Int,
            val page: QuizPage
        ) : QuizUi()
        data class Result(val correct: Int, val total: Int) : QuizUi()
    }

    sealed class QuizPage {
        data class Intro(val direction: String?, val passageTitle: String?, val passage: String?) : QuizPage()
        data class Question(
            val questionIndex: Int,
            val question: PyqpQuestion,
            val userAnswerIndex: Int?,
            val flagged: Boolean
        ) : QuizPage()
    }
}
