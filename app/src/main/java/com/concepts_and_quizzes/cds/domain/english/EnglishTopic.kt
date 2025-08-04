package com.concepts_and_quizzes.cds.domain.english

data class EnglishTopic(
    val id: String,
    val name: String,
    val overview: String,
    val isPremium: Boolean = false
)
