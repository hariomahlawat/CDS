package com.concepts_and_quizzes.cds.data.english.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.concepts_and_quizzes.cds.domain.english.EnglishTopic

@Entity(tableName = "english_topics")
data class EnglishTopicEntity(
    @PrimaryKey val id: String,
    val name: String,
    val overview: String,
    val isPremium: Boolean = false
)

fun EnglishTopicEntity.toDomain() = EnglishTopic(id, name, overview, isPremium)
