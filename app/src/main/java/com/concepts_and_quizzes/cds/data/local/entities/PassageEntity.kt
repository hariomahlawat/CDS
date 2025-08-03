package com.concepts_and_quizzes.cds.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "passages",
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
data class PassageEntity(
    @PrimaryKey val passageId: String,
    val examId: String,
    val title: String?,
    val text: String
)
