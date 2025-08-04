package com.concepts_and_quizzes.cds.domain.english

data class PyqpQuestion(
    val id: String,
    val text: String,
    val options: List<AnswerOption>,
    val direction: String? = null,
    val passage: String? = null,
    val passageTitle: String? = null
)

fun PyqpQuestion.shuffledOptions(): PyqpQuestion =
    copy(options = options.shuffled())
