package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Aggregated attempt statistics for a question.
 */
@Entity(tableName = "question_stats")
data class QuestionStatEntity(
    @PrimaryKey val qid: String,
    val correctCount: Int,
    val wrongCount: Int,
    val lastCorrect: Boolean
)

