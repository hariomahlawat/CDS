package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import com.concepts_and_quizzes.cds.data.english.model.EnglishTopicEntity
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity

class SeedUtil @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val db: EnglishDatabase
) {
    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        if (db.topicDao().count() == 0) {
            val json = ctx.assets.open("english_seed.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val topicsJson = root.getJSONArray("topics")
            val topics = mutableListOf<EnglishTopicEntity>()
            for (i in 0 until topicsJson.length()) {
                val t = topicsJson.getJSONObject(i)
                topics.add(
                    EnglishTopicEntity(
                        id = t.getString("id"),
                        name = t.getString("name"),
                        overview = t.getString("overview"),
                        isPremium = t.optBoolean("isPremium", false)
                    )
                )
            }
            val questionsJson = root.getJSONArray("questions")
            val questions = mutableListOf<EnglishQuestionEntity>()
            for (i in 0 until questionsJson.length()) {
                val q = questionsJson.getJSONObject(i)
                questions.add(
                    EnglishQuestionEntity(
                        qid = q.getString("qid"),
                        topicId = q.getString("topicId"),
                        question = q.getString("question"),
                        optionA = q.getString("optionA"),
                        optionB = q.getString("optionB"),
                        optionC = q.getString("optionC"),
                        optionD = q.getString("optionD"),
                        correct = q.getString("correct")
                    )
                )
            }
            db.topicDao().insertAll(topics)
            db.questionDao().insertAll(questions)
        }
        if (db.pyqpDao().count() == 0) {
            val file = "CDS_II_2024_English_SetA.json"
            val json = ctx.assets.open(file).bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val questionsJson = root.getJSONArray("questions")
            val qs = mutableListOf<PyqpQuestionEntity>()
            for (i in 0 until questionsJson.length()) {
                val q = questionsJson.getJSONObject(i)
                val opts = q.getJSONObject("options")
                val correct = when (q.getString("correct_answer")) {
                    "A" -> 0
                    "B" -> 1
                    "C" -> 2
                    else -> 3
                }
                qs.add(
                    PyqpQuestionEntity(
                        qid = "$file-${q.getInt("question_number")}",
                        paperId = file,
                        question = q.getString("question"),
                        optionA = opts.getString("A"),
                        optionB = opts.getString("B"),
                        optionC = opts.getString("C"),
                        optionD = opts.getString("D"),
                        correctIndex = correct
                    )
                )
            }
            db.pyqpDao().insertAll(qs)
        }
    }
}
