package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.SavedStateHandle
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import com.concepts_and_quizzes.cds.data.analytics.db.TopicDifficultyDb
import com.concepts_and_quizzes.cds.data.analytics.db.TopicTrendPointDb
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.data.analytics.db.TrendPoint
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTraceDao
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.english.db.PyqpDao
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.quiz.QuizResumeStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuizViewModelTest {
    private val questions = listOf(
        PyqpQuestionEntity("q1", "paper", "q1", "a", "b", "c", "d", 0),
        PyqpQuestionEntity("q2", "paper", "q2", "a", "b", "c", "d", 1),
        PyqpQuestionEntity("q3", "paper", "q3", "a", "b", "c", "d", 2)
    )

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun computesResult() = runTest {
        val dao = object : PyqpDao {
            override suspend fun insertAll(questions: List<PyqpQuestionEntity>) {}
            override fun getDistinctPaperIds(): Flow<List<String>> = flowOf(listOf("paper"))
            override fun getQuestionsByPaper(paperId: String): Flow<List<PyqpQuestionEntity>> = flowOf(questions)
            override suspend fun getQuestionsByIds(qids: List<String>): List<PyqpQuestionEntity> = emptyList()
            override suspend fun count(): Int = 0
        }
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll(): Flow<List<PyqpProgress>> = MutableStateFlow(emptyList())
        }
        val inserted = mutableListOf<AttemptLogEntity>()
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<AttemptLogEntity>) {
                inserted.addAll(attempts)
            }
            override suspend fun latestWrongQids(topicId: String): List<String> = emptyList()
            override fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>> = flowOf(emptyList())
            override fun getDifficulty(): Flow<List<TopicDifficultyDb>> = flowOf(emptyList())
            override fun getAttemptsWithScore(): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>> = flowOf(emptyList())
        }
        val topicStatDao = object : com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao {
            override fun topicSnapshot(cutoffTime: Long): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.TopicStat>> = flowOf(emptyList())
            override fun trendSnapshot(cutoffTime: Long): Flow<List<TrendPoint>> = flowOf(emptyList())
        }
        val analytics = AnalyticsRepository(attemptDao, topicStatDao)
        val repo = PyqpRepository(dao, attemptDao)
        val traces = mutableListOf<QuizTrace>()
        val traceDao = object : QuizTraceDao {
            override suspend fun insertTrace(trace: QuizTrace) { traces.add(trace) }
            override suspend fun tracesForSession(sid: String): List<QuizTrace> = traces.filter { it.sessionId == sid }
        }
        val reportRepo = QuizReportRepository(traceDao)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vm = QuizViewModel(repo, progressDao, analytics, reportRepo, QuizResumeStore(context), SavedStateHandle(mapOf("paperId" to "paper")))
        advanceUntilIdle()

        val q1 = vm.pageContent(0) as QuizViewModel.QuizPage.Question
        val idx0 = q1.question.options.indexOfFirst { it.isCorrect }
        vm.select(idx0)
        vm.next()

        val q2 = vm.pageContent(1) as QuizViewModel.QuizPage.Question
        val idx1 = q2.question.options.indexOfFirst { it.isCorrect }
        vm.select(idx1)
        vm.next()

        val q3 = vm.pageContent(2) as QuizViewModel.QuizPage.Question
        val correctIdx = q3.question.options.indexOfFirst { it.isCorrect }
        val wrongIdx = (correctIdx + 1) % q3.question.options.size
        vm.select(wrongIdx)
        vm.next()
        advanceUntilIdle()

        val res = vm.result.value!!
        assertEquals(2, res.correct)
        assertEquals(3, res.total)
        assertEquals(3, inserted.size)
    }

    @Test
    fun loadsWrongOnlyQuestions() = runTest {
        val dao = object : PyqpDao {
            override suspend fun insertAll(questions: List<PyqpQuestionEntity>) {}
            override fun getDistinctPaperIds(): Flow<List<String>> = flowOf(emptyList())
            override fun getQuestionsByPaper(paperId: String): Flow<List<PyqpQuestionEntity>> = flowOf(emptyList())
            override suspend fun getQuestionsByIds(qids: List<String>): List<PyqpQuestionEntity> =
                questions.filter { it.qid in qids }
            override suspend fun count(): Int = 0
        }
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll(): Flow<List<PyqpProgress>> = MutableStateFlow(emptyList())
        }
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<AttemptLogEntity>) {}
            override suspend fun latestWrongQids(topicId: String): List<String> = listOf("q1", "q2")
            override fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>> = flowOf(emptyList())
            override fun getDifficulty(): Flow<List<TopicDifficultyDb>> = flowOf(emptyList())
            override fun getAttemptsWithScore(): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>> = flowOf(emptyList())
        }
        val topicStatDao = object : com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao {
            override fun topicSnapshot(cutoffTime: Long): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.TopicStat>> = flowOf(emptyList())
            override fun trendSnapshot(cutoffTime: Long): Flow<List<TrendPoint>> = flowOf(emptyList())
        }
        val analytics = AnalyticsRepository(attemptDao, topicStatDao)
        val repo = PyqpRepository(dao, attemptDao)
        val traceDao = object : QuizTraceDao {
            override suspend fun insertTrace(trace: QuizTrace) {}
            override suspend fun tracesForSession(sid: String): List<QuizTrace> = emptyList()
        }
        val reportRepo = QuizReportRepository(traceDao)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vm = QuizViewModel(
            repo,
            progressDao,
            analytics,
            reportRepo,
            QuizResumeStore(context),
            SavedStateHandle(mapOf("mode" to "WRONGS", "topic" to "grammar"))
        )
        advanceUntilIdle()
        assertEquals(2, vm.questionCount)
    }

    @Test
    fun restoresSnapshotOnInit() = runTest {
        val dao = object : PyqpDao {
            override suspend fun insertAll(questions: List<PyqpQuestionEntity>) {}
            override fun getDistinctPaperIds(): Flow<List<String>> = flowOf(listOf("paper"))
            override fun getQuestionsByPaper(paperId: String): Flow<List<PyqpQuestionEntity>> = flowOf(questions)
            override suspend fun getQuestionsByIds(qids: List<String>): List<PyqpQuestionEntity> = emptyList()
            override suspend fun count(): Int = 0
        }
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll(): Flow<List<PyqpProgress>> = MutableStateFlow(emptyList())
        }
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<AttemptLogEntity>) {}
            override suspend fun latestWrongQids(topicId: String): List<String> = emptyList()
            override fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>> = flowOf(emptyList())
            override fun getDifficulty(): Flow<List<TopicDifficultyDb>> = flowOf(emptyList())
            override fun getAttemptsWithScore(): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>> = flowOf(emptyList())
        }
        val topicStatDao = object : com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao {
            override fun topicSnapshot(cutoffTime: Long): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.TopicStat>> = flowOf(emptyList())
            override fun trendSnapshot(cutoffTime: Long): Flow<List<TrendPoint>> = flowOf(emptyList())
        }
        val analytics = AnalyticsRepository(attemptDao, topicStatDao)
        val repo = PyqpRepository(dao, attemptDao)
        val traceDao = object : QuizTraceDao {
            override suspend fun insertTrace(trace: QuizTrace) {}
            override suspend fun tracesForSession(sid: String): List<QuizTrace> = emptyList()
        }
        val reportRepo = QuizReportRepository(traceDao)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val resumeStore = QuizResumeStore(context)
        resumeStore.save("paper", mapOf(1 to 2), setOf(1), 1, 0, mapOf(1 to 500))
        val vm = QuizViewModel(repo, progressDao, analytics, reportRepo, resumeStore, SavedStateHandle(mapOf("paperId" to "paper")))
        advanceUntilIdle()
        val ui = vm.ui.value as QuizViewModel.QuizUi.Page
        assertEquals(1, ui.pageIndex)
        val page = vm.pageContent(1) as QuizViewModel.QuizPage.Question
        assertEquals(2, page.userAnswerIndex)
    }
}
