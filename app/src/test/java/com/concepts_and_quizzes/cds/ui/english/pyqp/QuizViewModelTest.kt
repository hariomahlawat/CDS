package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.lifecycle.SavedStateHandle
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import com.concepts_and_quizzes.cds.data.analytics.db.TopicDifficultyDb
import com.concepts_and_quizzes.cds.data.analytics.db.TopicSnapshotDb
import com.concepts_and_quizzes.cds.data.analytics.db.TopicTrendPointDb
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.data.english.db.PyqpDao
import com.concepts_and_quizzes.cds.data.english.db.PyqpProgressDao
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
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
            override suspend fun count(): Int = 0
        }
        val repo = PyqpRepository(dao)
        val progressDao = object : PyqpProgressDao {
            override suspend fun upsert(progress: PyqpProgress) {}
            override fun getAll(): Flow<List<PyqpProgress>> = MutableStateFlow(emptyList())
        }
        val inserted = mutableListOf<AttemptLogEntity>()
        val attemptDao = object : AttemptLogDao {
            override suspend fun insertAll(attempts: List<AttemptLogEntity>) {
                inserted.addAll(attempts)
            }
            override fun getTopicSnapshot(): Flow<List<TopicSnapshotDb>> = flowOf(emptyList())
            override fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>> = flowOf(emptyList())
            override fun getDifficulty(): Flow<List<TopicDifficultyDb>> = flowOf(emptyList())
            override fun getAttemptsWithScore(): Flow<List<com.concepts_and_quizzes.cds.data.analytics.db.AttemptWithScoreDb>> = flowOf(emptyList())
        }
        val analytics = AnalyticsRepository(attemptDao)
        val vm = QuizViewModel(repo, progressDao, analytics, SavedStateHandle(mapOf("paperId" to "paper")))
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

        val res = vm.ui.value as QuizViewModel.QuizUi.Result
        assertEquals(2, res.correct)
        assertEquals(3, res.total)
        assertEquals(3, inserted.size)
    }
}
