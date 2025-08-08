package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import android.content.Context
import com.concepts_and_quizzes.cds.data.english.db.PyqpDao
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.TopicTrendPointDb
import com.concepts_and_quizzes.cds.data.analytics.db.TopicDifficultyDb
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.data.analytics.db.TrendPoint
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTraceDao
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.data.quiz.QuizResumeStore
import androidx.lifecycle.SavedStateHandle

class ResumeFlowUiTest {
    @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun resumeStartsAtSavedQuestion() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val questions = listOf(
            PyqpQuestionEntity("q1", "paper", "q1", "a", "b", "c", "d", 0),
            PyqpQuestionEntity("q2", "paper", "q2", "a", "b", "c", "d", 1),
            PyqpQuestionEntity("q3", "paper", "q3", "a", "b", "c", "d", 2)
        )
        val dao = object : PyqpDao {
            override suspend fun insertAll(questions: List<PyqpQuestionEntity>) {}
            override fun getDistinctPaperIds() = kotlinx.coroutines.flow.flowOf(listOf("paper"))
            override fun getQuestionsByPaper(paperId: String) = kotlinx.coroutines.flow.flowOf(questions)
            override suspend fun getQuestionsByIds(qids: List<String>) = emptyList<PyqpQuestionEntity>()
            override suspend fun count() = 0
        }
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll() = kotlinx.coroutines.flow.MutableStateFlow(emptyList<PyqpProgress>())
        }
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity>) {}
            override suspend fun upsertAll(rows: List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity>) {}
            override suspend fun forSession(sid: String): List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity> = emptyList()
            override suspend fun latestWrongQids(topicId: String) = emptyList<String>()
            override suspend fun latestWrongQids() = emptyList<String>()
            override fun getTrend(startTime: Long) = kotlinx.coroutines.flow.flowOf(emptyList<TopicTrendPointDb>())
            override fun getDifficulty() = kotlinx.coroutines.flow.flowOf(emptyList<TopicDifficultyDb>())
            override fun getAttemptsWithScore() = kotlinx.coroutines.flow.flowOf(emptyList<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>())
        }
        val topicStatDao = object : com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao {
            override fun topicSnapshot(cutoffTime: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.concepts_and_quizzes.cds.data.analytics.db.TopicStat>())
            override fun trendSnapshot(cutoffTime: Long) = kotlinx.coroutines.flow.flowOf(emptyList<TrendPoint>())
        }
        val analytics = AnalyticsRepository(attemptDao, topicStatDao)
        val repo = PyqpRepository(dao, attemptDao)
        val traceDao = object : QuizTraceDao {
            override suspend fun insertTrace(trace: QuizTrace) {}
            override suspend fun tracesForSession(sid: String) = emptyList<QuizTrace>()
            override suspend fun latestSessionId(): String? = null
        }
        val reportRepo = QuizReportRepository(traceDao)
        val resumeStore = QuizResumeStore(context)
        resumeStore.save("paper", emptyMap(), emptySet(), 2, 0)
        val vm = QuizViewModel(repo, progressDao, analytics, reportRepo, resumeStore, SavedStateHandle(mapOf("paperId" to "paper")))

        composeRule.setContent {
            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = "dashboard") {
                composable("dashboard") {
                    ResumeButton { nav.navigate("quiz") }
                }
                composable("quiz") {
                    QuizScreen(paperId = "paper", nav = nav, vm = vm)
                }
            }
        }

        composeRule.onNodeWithText("Resume").performClick()
        composeRule.onNodeWithText("q3").assertIsDisplayed()
    }
}

@Composable
private fun ResumeButton(onClick: () -> Unit) {
    androidx.compose.material3.Button(onClick = onClick) {
        Text("Resume")
    }
}
