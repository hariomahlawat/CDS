package com.concepts_and_quizzes.cds.domain.analytics

/**
 * Average difficulty per topic. Lower values mean harder questions.
 */
data class TopicDifficulty(
    val topicId: String,
    val difficulty: Double
)
