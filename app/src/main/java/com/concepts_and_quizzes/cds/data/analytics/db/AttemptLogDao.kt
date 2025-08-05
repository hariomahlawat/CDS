package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [AttemptLogEntity] and analytics queries.
 */
@Dao
interface AttemptLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attempts: List<AttemptLogEntity>)

    // --- Topic snapshot ---
    @Query(
        """
        SELECT q.topicId AS topicId,
               COUNT(*) AS total,
               SUM(CASE WHEN a.correct THEN 1 ELSE 0 END) AS correct,
               AVG(a.durationMs) AS avgDurationMs,
               SUM(CASE WHEN a.flagged THEN 1 ELSE 0 END) AS flagged
        FROM attempt_log a
        JOIN english_questions q ON a.qid = q.qid
        GROUP BY q.topicId
        """
    )
    fun getTopicSnapshot(): Flow<List<TopicSnapshotDb>>

    // --- Trend over time ---
    @Query(
        """
        SELECT q.topicId AS topicId,
               strftime('%Y-%m-%d', a.timestamp/1000, 'unixepoch') AS day,
               COUNT(*) AS total,
               SUM(CASE WHEN a.correct THEN 1 ELSE 0 END) AS correct
        FROM attempt_log a
        JOIN english_questions q ON a.qid = q.qid
        WHERE a.timestamp >= :startTime
        GROUP BY q.topicId, day
        ORDER BY day
        """
    )
    fun getTrend(startTime: Long): Flow<List<TopicTrendPointDb>>

    // --- Difficulty per topic ---
    @Query(
        """
        WITH per_question AS (
            SELECT q.qid AS qid,
                   q.topicId AS topicId,
                   AVG(CASE WHEN a.correct THEN 1.0 ELSE 0.0 END) AS p
            FROM attempt_log a
            JOIN english_questions q ON a.qid = q.qid
            GROUP BY q.qid
        )
        SELECT topicId,
               AVG(p) AS difficulty
        FROM per_question
        GROUP BY topicId
        """
    )
    fun getDifficulty(): Flow<List<TopicDifficultyDb>>

    // --- Attempts with quiz total score for discrimination computation ---
    @Query(
        """
        WITH quiz_scores AS (
            SELECT quizId,
                   SUM(CASE WHEN correct THEN 1 ELSE 0 END) AS totalScore
            FROM attempt_log
            GROUP BY quizId
        )
        SELECT a.qid AS qid,
               a.correct AS correct,
               qs.totalScore AS totalScore
        FROM attempt_log a
        JOIN quiz_scores qs ON a.quizId = qs.quizId
        """
    )
    fun getAttemptsWithScore(): Flow<List<AttemptWithScoreDb>>
}

// --- Data classes for query results ---

data class TopicSnapshotDb(
    val topicId: String,
    val total: Int,
    val correct: Int,
    val avgDurationMs: Double,
    val flagged: Int
)

data class TopicTrendPointDb(
    val topicId: String,
    val day: String,
    val total: Int,
    val correct: Int
)

data class TopicDifficultyDb(
    val topicId: String,
    val difficulty: Double
)

data class AttemptWithScoreDb(
    val qid: String,
    val correct: Boolean,
    val totalScore: Int
)
