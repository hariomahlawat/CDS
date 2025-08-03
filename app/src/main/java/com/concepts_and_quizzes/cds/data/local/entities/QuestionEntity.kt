package com.concepts_and_quizzes.cds.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["examId"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DirectionEntity::class,
            parentColumns = ["directionId"],
            childColumns = ["directionId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = PassageEntity::class,
            parentColumns = ["passageId"],
            childColumns = ["passageId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("examId"), Index("directionId"), Index("passageId")]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: String,
    val questionNumber: Int,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val topic: String?,
    val subTopic: String?,
    val difficulty: String?,
    val remarks: String?,
    val passageId: String?,
    val directionId: String?
)
