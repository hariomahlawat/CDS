package com.concepts_and_quizzes.cds.domain.analytics

/**
 * Aggregated statistics for a single topic.
 */
data class TopicStat(
    val topicId: String,
    val total: Int,
    val correct: Int,
    val avgDurationMs: Double,
    val flagged: Int
)
