package com.concepts_and_quizzes.cds.data.english.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pyqp_questions")
data class PyqpQuestionEntity(
    @PrimaryKey val qid: String,
    val paperId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val direction: String? = null,
    val passageTitle: String? = null,
    val passageText: String? = null,
    val topic: String = "",
    val subTopic: String = ""
)
