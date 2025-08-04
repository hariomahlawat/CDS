package com.concepts_and_quizzes.cds.data.english.pyqp

import android.content.Context
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PyqpFileDto(
    @SerialName("exam_details") val exam: ExamDto,
    val questions: List<PyqpQuestionDto>
)

@Serializable
data class ExamDto(@SerialName("exam_id") val examId: String)

@Serializable
data class PyqpQuestionDto(
    @SerialName("question_number") val number: Int,
    val question: String,
    val options: OptionsDto,
    @SerialName("correct_answer") val correctAnswer: String
)

@Serializable
data class OptionsDto(
    @SerialName("A") val optionA: String,
    @SerialName("B") val optionB: String,
    @SerialName("C") val optionC: String,
    @SerialName("D") val optionD: String
)

fun PyqpQuestionDto.toEntity(paperId: String) = PyqpQuestionEntity(
    qid = "$paperId-$number",
    paperId = paperId,
    question = question,
    optionA = options.optionA,
    optionB = options.optionB,
    optionC = options.optionC,
    optionD = options.optionD,
    correct = correctAnswer
)

object PyqpJsonReader {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(ctx: Context, path: String): Pair<String, List<PyqpQuestionDto>> =
        ctx.assets.open(path).bufferedReader().use { reader ->
            val dto = json.decodeFromString<PyqpFileDto>(reader.readText())
            dto.exam.examId to dto.questions
        }
}
