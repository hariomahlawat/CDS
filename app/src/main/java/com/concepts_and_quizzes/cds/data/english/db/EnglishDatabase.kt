package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity
import com.concepts_and_quizzes.cds.data.DateConverters
import com.concepts_and_quizzes.cds.data.discover.db.ConceptDao
import com.concepts_and_quizzes.cds.data.discover.model.BookmarkEntity
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity
import com.concepts_and_quizzes.cds.data.discover.model.DailyTipEntity
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import com.concepts_and_quizzes.cds.data.english.model.EnglishTopicEntity
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity

@Database(
    entities = [
        EnglishTopicEntity::class,
        EnglishQuestionEntity::class,
        PyqpQuestionEntity::class,
        PyqpProgress::class,
        AttemptLogEntity::class,
        ConceptEntity::class,
        DailyTipEntity::class,
        BookmarkEntity::class
    ],
    version = 7
)
@TypeConverters(DateConverters::class)
abstract class EnglishDatabase : RoomDatabase() {
    abstract fun topicDao(): EnglishTopicDao
    abstract fun questionDao(): EnglishQuestionDao
    abstract fun pyqpDao(): PyqpDao
    abstract fun pyqpProgressDao(): PyqpProgressDao
    abstract fun attemptLogDao(): com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
    abstract fun topicStatDao(): com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao
    abstract fun conceptDao(): ConceptDao
}
