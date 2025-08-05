package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import com.concepts_and_quizzes.cds.data.english.model.EnglishTopicEntity
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity

@Database(
    entities = [
        EnglishTopicEntity::class,
        EnglishQuestionEntity::class,
        PyqpQuestionEntity::class,
        PyqpProgress::class,
        AttemptLogEntity::class
    ],
    version = 6
)
abstract class EnglishDatabase : RoomDatabase() {
    abstract fun topicDao(): EnglishTopicDao
    abstract fun questionDao(): EnglishQuestionDao
    abstract fun pyqpDao(): PyqpDao
    abstract fun pyqpProgressDao(): PyqpProgressDao
    abstract fun attemptLogDao(): com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
    abstract fun topicStatDao(): com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao
}
