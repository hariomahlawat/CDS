package com.concepts_and_quizzes.cds.data.english.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.concepts_and_quizzes.cds.domain.english.EnglishQuestion

@Entity(tableName = "english_questions")
data class EnglishQuestionEntity(
    @PrimaryKey val qid: String,
    val topicId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correct: String
)

fun EnglishQuestionEntity.toDomain() = EnglishQuestion(
    qid,
    topicId,
    question,
    optionA,
    optionB,
    optionC,
    optionD,
    correct
)
