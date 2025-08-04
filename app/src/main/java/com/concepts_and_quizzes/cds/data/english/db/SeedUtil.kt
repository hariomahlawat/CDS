package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import com.concepts_and_quizzes.cds.data.english.model.EnglishTopicEntity

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
    }
}
