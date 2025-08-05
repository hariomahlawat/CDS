package com.concepts_and_quizzes.cds.domain.analytics

/**
 * Represents performance for a topic on a particular day.
 */
data class TopicTrendPoint(
    val topicId: String,
    val day: String,
    val total: Int,
    val correct: Int
)
