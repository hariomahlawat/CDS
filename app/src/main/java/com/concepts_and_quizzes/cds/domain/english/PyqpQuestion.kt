package com.concepts_and_quizzes.cds.domain.english

data class PyqpQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correct: Int
)
