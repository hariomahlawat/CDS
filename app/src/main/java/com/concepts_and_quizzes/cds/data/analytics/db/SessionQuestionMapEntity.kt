package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Entity

@Entity(
    tableName = "session_question_map",
    primaryKeys = ["sessionId", "questionIndex"]
)
data class SessionQuestionMapEntity(
    val sessionId: String,
    val questionIndex: Int,
    val questionId: String
)
