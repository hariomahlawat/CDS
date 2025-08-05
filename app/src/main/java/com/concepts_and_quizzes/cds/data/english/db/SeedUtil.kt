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
            val directionsMap = mutableMapOf<String, String>()
            val directionsJson = root.optJSONArray("directions")
            if (directionsJson != null) {
                for (i in 0 until directionsJson.length()) {
                    val d = directionsJson.getJSONObject(i)
                    directionsMap[d.getString("direction_id")] = d.getString("text")
                }
            }
            val passagesMap = mutableMapOf<String, Pair<String, String>>()
            val passagesJson = root.optJSONArray("passages")
            if (passagesJson != null) {
                for (i in 0 until passagesJson.length()) {
                    val p = passagesJson.getJSONObject(i)
                    passagesMap[p.getString("passage_id")] =
                        p.getString("title") to p.getString("text")
                }
            }
            val questionsJson = root.getJSONArray("questions")
            val qs = mutableListOf<PyqpQuestionEntity>()
            for (i in 0 until questionsJson.length()) {
                try {
                    val q = questionsJson.getJSONObject(i)
                    val opts = q.optJSONObject("options") ?: JSONObject()
                    val correct = when (q.optString("correct_answer")) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> 3
                    }
                    val dirText = directionsMap[q.optString("direction_id")]
                    val passageId = q.optString("passage_id")
                    val passage = passagesMap[passageId]
                    qs.add(
                        PyqpQuestionEntity(
                            qid = "$file-${q.getInt("question_number")}",
                            paperId = file,
                            question = q.getString("question"),
                            optionA = opts.optString("A"),
                            optionB = opts.optString("B"),
                            optionC = opts.optString("C"),
                            optionD = opts.optString("D"),
                            correctIndex = correct,
                            direction = dirText,
                            passageTitle = passage?.first,
                            passageText = passage?.second,
                            topic = q.optString("topic"),
                            subTopic = q.optString("sub_topic")
                        )
                    )
                } catch (e: org.json.JSONException) {
                    // Skip malformed questions instead of crashing the seeding process
                    continue
                }
            }
            db.pyqpDao().insertAll(qs)
        }
    }
}
