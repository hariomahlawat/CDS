package com.concepts_and_quizzes.cds.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class QuestionWithDirectionAndPassage(
    @Embedded val question: QuestionEntity,
    @Relation(
        parentColumn = "directionId",
        entityColumn = "directionId"
    )
    val direction: DirectionEntity?,
    @Relation(
        parentColumn = "passageId",
        entityColumn = "passageId"
    )
    val passage: PassageEntity?
)
