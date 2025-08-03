package com.concepts_and_quizzes.cds.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "directions",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["examId"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("examId")]
)
data class DirectionEntity(
    @PrimaryKey val directionId: String,
    val examId: String,
    val section: String?,
    val text: String
)
