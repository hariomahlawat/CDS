package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.room.Room
import com.concepts_and_quizzes.cds.data.english.repo.EnglishRepository
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
        Room.databaseBuilder(ctx, EnglishDatabase::class.java, "english.db").build()

    @Provides
    fun provideTopicDao(db: EnglishDatabase): EnglishTopicDao = db.topicDao()

    @Provides
    fun provideQuestionDao(db: EnglishDatabase): EnglishQuestionDao = db.questionDao()

    @Provides
    @Singleton
    fun provideEnglishRepository(
        topicDao: EnglishTopicDao,
        questionDao: EnglishQuestionDao
    ): EnglishRepository = EnglishRepository(topicDao, questionDao)
}
