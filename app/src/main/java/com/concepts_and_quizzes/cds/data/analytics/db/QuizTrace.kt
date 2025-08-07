package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Entity

/**
 * Trace of a single question within a quiz session.
 * Uses [sessionId] + [questionId] as a composite primary key to allow
 * deterministic insertion per question.
 */
@Entity(tableName = "quiz_trace", primaryKeys = ["sessionId", "questionId"])
data class QuizTrace(
    val sessionId: String,
    val questionId: Int,
    val topicId: Int,
    val startedAt: Long,
    val answeredAt: Long,
    val isCorrect: Boolean
)
