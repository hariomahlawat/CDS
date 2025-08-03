package com.concepts_and_quizzes.cds.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val examId: String,
    val year: Int,
    val session: String,
    val subject: String,
    val totalQuestions: Int,
    val maxMarks: Int,
    val examDate: String
)
