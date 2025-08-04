package com.concepts_and_quizzes.cds.data.english.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pyqp_progress")
data class PyqpProgress(
    @PrimaryKey val paperId: String,
    val correct: Int,
    val attempted: Int
)
