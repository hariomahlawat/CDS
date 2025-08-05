package com.concepts_and_quizzes.cds.domain.analytics

/**
 * Point-biserial discrimination index for a question.
 */
data class QuestionDiscrimination(
    val qid: String,
    val discrimination: Double
)
