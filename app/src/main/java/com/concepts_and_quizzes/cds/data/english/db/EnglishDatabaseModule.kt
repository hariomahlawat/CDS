package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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
        Room.databaseBuilder(ctx, EnglishDatabase::class.java, "english.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTopicDao(db: EnglishDatabase): EnglishTopicDao = db.topicDao()

    @Provides
    fun provideQuestionDao(db: EnglishDatabase): EnglishQuestionDao = db.questionDao()

    @Provides
    fun providePyqpQuestionDao(db: EnglishDatabase): PyqpQuestionDao = db.pyqpQuestionDao()

    @Provides
    fun providePyqpImportLogDao(db: EnglishDatabase): PyqpImportLogDao = db.pyqpImportLogDao()

    @Provides
    @Singleton
    fun provideImportDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            ctx.preferencesDataStoreFile("pyqp_imports")
        }

    @Provides
    @Singleton
    fun provideEnglishRepository(
        topicDao: EnglishTopicDao,
        questionDao: EnglishQuestionDao
    ): EnglishRepository = EnglishRepository(topicDao, questionDao)
}
