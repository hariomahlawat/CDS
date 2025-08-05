package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import com.concepts_and_quizzes.cds.domain.analytics.QuestionDiscrimination
import com.concepts_and_quizzes.cds.domain.analytics.TopicDifficulty
import com.concepts_and_quizzes.cds.domain.analytics.TopicStat
import com.concepts_and_quizzes.cds.domain.analytics.TopicTrendPoint
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Repository exposing analytics metrics as [Flow]s.
 */
class AnalyticsRepository @Inject constructor(
    private val attemptDao: AttemptLogDao
) {
    suspend fun insertAttempts(attempts: List<AttemptLogEntity>) =
        attemptDao.insertAll(attempts)

    fun getTopicSnapshot(): Flow<List<TopicStat>> =
        attemptDao.getTopicSnapshot()
            .map { list ->
                list.map {
                    TopicStat(it.topicId, it.total, it.correct, it.avgDurationMs, it.flagged)
                }
            }
            .flowOn(Dispatchers.IO)

    fun getTrend(periodDays: Int = 30): Flow<List<TopicTrendPoint>> {
        val startTime = System.currentTimeMillis() - periodDays * 24L * 60 * 60 * 1000
        return attemptDao.getTrend(startTime)
            .map { list ->
                list.map { TopicTrendPoint(it.topicId, it.day, it.total, it.correct) }
            }
            .flowOn(Dispatchers.IO)
    }

    fun getDifficulty(): Flow<List<TopicDifficulty>> =
        attemptDao.getDifficulty()
            .map { list ->
                list.map { TopicDifficulty(it.topicId, it.difficulty) }
            }
            .flowOn(Dispatchers.IO)

    fun getDiscrimination(): Flow<List<QuestionDiscrimination>> =
        attemptDao.getAttemptsWithScore()
            .map { list ->
                val grouped = list.groupBy { it.qid }
                grouped.map { (qid, attempts) ->
                    val n = attempts.size
                    if (n == 0) {
                        QuestionDiscrimination(qid, 0.0)
                    } else {
                        val xs = attempts.map { if (it.correct) 1.0 else 0.0 }
                        val ys = attempts.map { it.totalScore.toDouble() }
                        val meanX = xs.average()
                        val meanY = ys.average()
                        val covariance = xs.indices.sumOf { i ->
                            (xs[i] - meanX) * (ys[i] - meanY)
                        } / n
                        val stdX = sqrt(xs.sumOf { (it - meanX).pow(2) } / n)
                        val stdY = sqrt(ys.sumOf { (it - meanY).pow(2) } / n)
                        val corr = if (stdX == 0.0 || stdY == 0.0) 0.0 else covariance / (stdX * stdY)
                        QuestionDiscrimination(qid, corr)
                    }
                }
            }
            .flowOn(Dispatchers.IO)
}
