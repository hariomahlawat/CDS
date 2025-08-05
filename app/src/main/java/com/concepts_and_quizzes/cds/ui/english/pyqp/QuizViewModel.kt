package com.concepts_and_quizzes.cds.ui.english.pyqp

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.data.quiz.QuizResumeStore
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: PyqpRepository,
    private val progressDao: PyqpProgressDao,
    private val analytics: AnalyticsRepository,
    private val resumeStore: QuizResumeStore,
    private val state: SavedStateHandle
) : ViewModel() {
    private val paperId: String = state["paperId"]!!

    private val _ui = MutableStateFlow<QuizUi>(QuizUi.Loading)
    val ui: StateFlow<QuizUi> = _ui

    private var pageIndex = 0
    private var pages: List<Item> = emptyList()
    private var questions: List<PyqpQuestion> = emptyList()
    private val answers = mutableMapOf<Int, Int>()
    private val flags = mutableSetOf<Int>()
    private val durations = mutableMapOf<Int, Int>()
    private var currentQuestion: Int? = null
    private var questionStartMs = SystemClock.elapsedRealtime()

    private val _showResult = MutableStateFlow(state["showResult"] ?: false)
    val showResult: StateFlow<Boolean> = _showResult

    private val _result = MutableStateFlow<QuizResult?>(null)
    val result: StateFlow<QuizResult?> = _result

    private val _timer = MutableStateFlow(state["timerSec"] ?: 120 * 60)
    val timer: StateFlow<Int> = _timer
    private var timerJob: Job? = null

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
                    resume()
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
            is Item.Intro -> {
                currentQuestion = null
                _ui.value = QuizUi.Page(
                    pageIndex,
                    pages.size,
                    questions.size,
                    QuizPage.Intro(item.direction, item.passageTitle, item.passage)
                )
            }
            is Item.Question -> {
                currentQuestion = item.questionIndex
                questionStartMs = SystemClock.elapsedRealtime()
                _ui.value = QuizUi.Page(
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
    }

    private fun flushDuration() {
        val idx = currentQuestion ?: return
        val now = SystemClock.elapsedRealtime()
        val elapsed = (now - questionStartMs).toInt()
        durations[idx] = durations.getOrDefault(idx, 0) + elapsed
        questionStartMs = now
    }

    private fun startTimer() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_timer.value > 0) {
                delay(1_000)
                val next = _timer.value - 1
                _timer.value = next
                if (next % 60 == 0) {
                    state["timerSec"] = next
                }
                if (next % 30 == 0) {
                    resumeStore.save(QuizResumeStore.Store(paperId, snapshot()))
                }
                if (next == 0) {
                    submitQuiz()
                    break
                }
            }
        }
    }

    private fun snapshot(): String {
        val ans = answers.entries.joinToString(";") { "${it.key}:${it.value}" }
        val flg = flags.joinToString(",")
        val dur = durations.entries.joinToString(";") { "${it.key}:${it.value}" }
        return listOf(pageIndex, ans, flg, dur, _timer.value).joinToString("|")
    }

    fun pause() {
        flushDuration()
        timerJob?.cancel()
        timerJob = null
        state["timerSec"] = _timer.value
        resumeStore.save(QuizResumeStore.Store(paperId, snapshot()))
    }

    fun resume() {
        if (_timer.value > 0) {
            questionStartMs = SystemClock.elapsedRealtime()
            startTimer()
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
            flushDuration()
            pageIndex++
            emitPage()
        } else {
            flushDuration()
            submitQuiz()
        }
    }

    fun prev() {
        if (pageIndex > 0) {
            flushDuration()
            pageIndex--
            emitPage()
        }
    }

    fun goTo(i: Int) {
        flushDuration()
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

    fun goToQuestion(questionIndex: Int) {
        val page = pages.indexOfFirst { it is Item.Question && it.questionIndex == questionIndex }
        if (page != -1) {
            flushDuration()
            pageIndex = page
            emitPage()
        }
    }

    data class PaletteEntry(val questionIndex: Int, val answered: Boolean, val flagged: Boolean)

    fun questionPalette(): List<PaletteEntry> {
        return (0 until questionCount).map { i ->
            PaletteEntry(i, answers.containsKey(i), flags.contains(i))
        }
    }

    fun submitQuiz() {
        timerJob?.cancel()
        timerJob = null
        flushDuration()
        val now = System.currentTimeMillis()
        val attempts = questions.mapIndexed { i, q ->
            val ansIdx = answers[i]
            val correct = ansIdx != null && q.options[ansIdx].isCorrect
            AttemptLogEntity(
                qid = q.id,
                quizId = paperId,
                correct = correct,
                flagged = flags.contains(i),
                durationMs = durations[i] ?: 0,
                timestamp = now
            )
        }
        viewModelScope.launch { analytics.insertAttempts(attempts) }
        _result.value = QuizResult(attempts.count { it.correct }, questions.size)
        _showResult.value = true
        state["showResult"] = true
    }

    fun dismissResult() {
        _showResult.value = false
        state["showResult"] = false
    }

    fun flush() = flushDuration()

    fun saveProgress() {
        val correct = answers.count { (i, ans) -> questions[i].options[ans].isCorrect }
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
    }

    data class QuizResult(val correct: Int, val total: Int)

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
