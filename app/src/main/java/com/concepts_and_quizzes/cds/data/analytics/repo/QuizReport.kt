package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace

/** Data class aggregating quiz KPIs. */
data class QuizReport(
    val total: Int,
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val strongestTopic: Int?,
    val weakestTopic: Int?,
    val timePerSection: List<TopicSummary>,
    val bottlenecks: List<QuizTrace>,
    val suggestions: List<String>,
    val unattempted: Int = total - attempted,
    val score: Float? = null,
    val sessionId: String? = null,
)

/** Per-topic summary used for charts. */
data class TopicSummary(
    val topicId: Int,
    val accuracy: Double,
    val avgTime: Double,
    val attempts: Int
)

val QuizReport.accuracy: Float
    get() = if (attempted == 0) 0f else correct.toFloat() / attempted.toFloat()
