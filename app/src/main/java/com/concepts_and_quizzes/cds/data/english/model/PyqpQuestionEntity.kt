package com.concepts_and_quizzes.cds.data.english.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion

@Entity(tableName = "pyqp_questions")
data class PyqpQuestionEntity(
    @PrimaryKey val qid: String,
    val paperId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int
)

fun PyqpQuestionEntity.toDomain() = PyqpQuestion(
    id = qid,
    text = question,
    options = listOf(optionA, optionB, optionC, optionD),
    correct = correctIndex
)
