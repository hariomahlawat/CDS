package com.concepts_and_quizzes.cds.data.discover.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "concept")
data class ConceptEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val blurb: String,
    val detail: String
)
