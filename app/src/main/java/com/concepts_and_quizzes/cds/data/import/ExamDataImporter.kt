package com.concepts_and_quizzes.cds.data.import

import android.content.Context
import com.concepts_and_quizzes.cds.data.local.entities.DirectionEntity
import com.concepts_and_quizzes.cds.data.local.entities.ExamEntity
import com.concepts_and_quizzes.cds.data.local.entities.PassageEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionEntity
import com.concepts_and_quizzes.cds.data.repository.ExamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class ParsedExam(
    val exam: ExamEntity,
    val directions: List<DirectionEntity>,
    val passages: List<PassageEntity>,
    val questions: List<QuestionEntity>
)

class ExamDataImporter @Inject constructor(
    private val context: Context,
    private val repository: ExamRepository
) {
    suspend fun importFromAssets(fileName: String) {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val parsed = parse(json)
        repository.insertFullExam(parsed.exam, parsed.directions, parsed.passages, parsed.questions)
    }

    private suspend fun parse(jsonString: String): ParsedExam = withContext(Dispatchers.Default) {
        val root = JSONObject(jsonString)
        val examObj = root.getJSONObject("exam")
        val exam = ExamEntity(
            examId = examObj.getString("examId"),
            year = examObj.getInt("year"),
            session = examObj.getString("session"),
            subject = examObj.getString("subject"),
            totalQuestions = examObj.getInt("totalQuestions"),
            maxMarks = examObj.getInt("maxMarks"),
            examDate = examObj.getString("examDate")
        )
        val directions = root.optJSONArray("directions")?.let { parseDirections(it, exam.examId) } ?: emptyList()
        val passages = root.optJSONArray("passages")?.let { parsePassages(it, exam.examId) } ?: emptyList()
        val questions = root.optJSONArray("questions")?.let { parseQuestions(it, exam.examId) } ?: emptyList()
        ParsedExam(exam, directions, passages, questions)
    }

    private fun parseDirections(array: JSONArray, examId: String): List<DirectionEntity> =
        List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            DirectionEntity(
                directionId = obj.getString("directionId"),
                examId = examId,
                section = obj.optString("section", null),
                text = obj.getString("text")
            )
        }

    private fun parsePassages(array: JSONArray, examId: String): List<PassageEntity> =
        List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            PassageEntity(
                passageId = obj.getString("passageId"),
                examId = examId,
                title = obj.optString("title", null),
                text = obj.getString("text")
            )
        }

    private fun parseQuestions(array: JSONArray, examId: String): List<QuestionEntity> =
        List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            QuestionEntity(
                examId = examId,
                questionNumber = obj.getInt("questionNumber"),
                question = obj.getString("question"),
                optionA = obj.getString("optionA"),
                optionB = obj.getString("optionB"),
                optionC = obj.getString("optionC"),
                optionD = obj.getString("optionD"),
                correctAnswer = obj.getString("correctAnswer"),
                topic = obj.optString("topic", null),
                subTopic = obj.optString("subTopic", null),
                difficulty = obj.optString("difficulty", null),
                remarks = obj.optString("remarks", null),
                passageId = obj.optString("passageId", null),
                directionId = obj.optString("directionId", null)
            )
        }
}
