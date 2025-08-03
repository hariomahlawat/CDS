package com.concepts_and_quizzes.cds.di

import android.content.Context
import com.concepts_and_quizzes.cds.data.local.AppDatabase
import com.concepts_and_quizzes.cds.data.local.dao.DirectionDao
import com.concepts_and_quizzes.cds.data.local.dao.ExamDao
import com.concepts_and_quizzes.cds.data.local.dao.PassageDao
import com.concepts_and_quizzes.cds.data.local.dao.QuestionDao
import com.concepts_and_quizzes.cds.data.repository.ExamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.create(context)

    @Provides
    fun provideExamDao(db: AppDatabase): ExamDao = db.examDao()

    @Provides
    fun provideDirectionDao(db: AppDatabase): DirectionDao = db.directionDao()

    @Provides
    fun providePassageDao(db: AppDatabase): PassageDao = db.passageDao()

    @Provides
    fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()

    @Provides
    @Singleton
    fun provideExamRepository(
        examDao: ExamDao,
        directionDao: DirectionDao,
        passageDao: PassageDao,
        questionDao: QuestionDao
    ): ExamRepository = ExamRepository(examDao, directionDao, passageDao, questionDao)
}
