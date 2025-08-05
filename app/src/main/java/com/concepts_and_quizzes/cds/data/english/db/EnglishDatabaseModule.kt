package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.room.Room
import com.concepts_and_quizzes.cds.data.english.repo.EnglishRepository
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnglishDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): EnglishDatabase =
        Room.databaseBuilder(ctx, EnglishDatabase::class.java, "english.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTopicDao(db: EnglishDatabase): EnglishTopicDao = db.topicDao()

    @Provides
    fun provideQuestionDao(db: EnglishDatabase): EnglishQuestionDao = db.questionDao()

    @Provides
    fun providePyqpDao(db: EnglishDatabase): PyqpDao = db.pyqpDao()

    @Provides
    fun providePyqpProgressDao(db: EnglishDatabase): PyqpProgressDao = db.pyqpProgressDao()

    @Provides
    fun provideAttemptLogDao(db: EnglishDatabase): AttemptLogDao = db.attemptLogDao()

    @Provides
    fun provideTopicStatDao(db: EnglishDatabase): TopicStatDao = db.topicStatDao()

    @Provides
    @Singleton
    fun provideEnglishRepository(
        topicDao: EnglishTopicDao,
        questionDao: EnglishQuestionDao
    ): EnglishRepository = EnglishRepository(topicDao, questionDao)

    @Provides
    @Singleton
    fun providePyqpRepository(
        pyqpDao: PyqpDao,
        attemptDao: AttemptLogDao
    ): PyqpRepository = PyqpRepository(pyqpDao, attemptDao)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        attemptDao: AttemptLogDao,
        topicStatDao: TopicStatDao
    ): AnalyticsRepository =
        AnalyticsRepository(attemptDao, topicStatDao)
}
