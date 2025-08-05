package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Analytics queries for PYQ topic accuracy statistics.
 */
@Dao
interface TopicStatDao {
    @Query(
        """
        SELECT q.topic AS topic,
               COUNT(*) AS total,
               SUM(a.correct) AS correct
        FROM attempt_log AS a
        JOIN pyqp_questions AS q ON q.qid = a.qid
        WHERE a.timestamp >= :cutoffTime
        GROUP BY q.topic
        ORDER BY (SUM(a.correct) * 1.0 / COUNT(*)) DESC
        """
    )
    fun topicSnapshot(cutoffTime: Long): Flow<List<TopicStat>>
}

/**
 * Aggregated stats for a single topic.
 */
data class TopicStat(
    val topic: String,
    val total: Int,
    val correct: Int
) {
    val percent: Float get() = if (total == 0) 0f else correct * 100f / total
}
