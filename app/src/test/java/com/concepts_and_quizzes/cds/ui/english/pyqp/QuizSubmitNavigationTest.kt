package com.concepts_and_quizzes.cds.ui.english.pyqp

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.test.core.app.ApplicationProvider
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuizSubmitNavigationTest {
    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun navigatesToReportsPage() = runTest {
        val dao = object : PyqpDao {
            override suspend fun insertAll(questions: List<PyqpQuestionEntity>) {}
            override fun getDistinctPaperIds(): Flow<List<String>> = flowOf(emptyList())
            override fun getQuestionsByPaper(paperId: String): Flow<List<PyqpQuestionEntity>> = flowOf(emptyList())
            override suspend fun getQuestionsByIds(qids: List<String>): List<PyqpQuestionEntity> = emptyList()
            override suspend fun count(): Int = 0
        }
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll(): Flow<List<PyqpProgress>> = MutableStateFlow(emptyList())
        }
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<AttemptLogEntity>) {}
            override suspend fun upsertAll(rows: List<AttemptLogEntity>) {}
            override suspend fun forSession(sid: String): List<AttemptLogEntity> = emptyList()
            override suspend fun latestWrongQids(topicId: String): List<String> = emptyList()
            override suspend fun latestWrongQids(): List<String> = emptyList()
            override fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>> = flowOf(emptyList())
            override fun getDifficulty(): Flow<List<TopicDifficultyDb>> = flowOf(emptyList())
            override fun getAttemptsWithScore(): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>> =
                flowOf(emptyList())
        }
        val topicStatDao = object : com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao {
            override fun topicSnapshot(cutoffTime: Long): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.TopicStat>> =
                flowOf(emptyList())
            override fun trendSnapshot(cutoffTime: Long): Flow<List<TrendPoint>> = flowOf(emptyList())
        }
        val analytics = AnalyticsRepository(attemptDao, topicStatDao)
        val repo = PyqpRepository(dao, attemptDao)
        val traceDao = object : QuizTraceDao {
            override suspend fun insertTrace(trace: QuizTrace) {}
            override suspend fun tracesForSession(sid: String): List<QuizTrace> = emptyList()
            override suspend fun latestSessionId(): String? = null
        }
        val reportRepo = QuizReportRepository(traceDao)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vm = QuizViewModel(
            repo,
            progressDao,
            analytics,
            reportRepo,
            QuizResumeStore(context),
            SavedStateHandle(mapOf("paperId" to "paper"))
        )
        advanceUntilIdle()

        val nav = object : NavHostController(context) {
            var route: String? = null
            override fun navigate(route: String, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?) {
                this.route = route
            }
        }

        vm.onSubmitSuccess(nav)
        assertTrue(nav.route!!.startsWith("reports?analysisSessionId="))
        assertTrue(nav.route!!.contains("&startPage=0"))
    }
}

