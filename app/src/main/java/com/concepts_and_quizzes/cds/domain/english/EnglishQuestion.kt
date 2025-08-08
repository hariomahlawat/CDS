package com.concepts_and_quizzes.cds.domain.english

data class EnglishQuestion(
    val qid: String,
    val topicId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correct: String,
    val subTopic: String = "",
    val difficulty: Int = 0
)
