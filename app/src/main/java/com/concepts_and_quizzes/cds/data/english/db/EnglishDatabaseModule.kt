package com.concepts_and_quizzes.cds.data.english.db

import android.content.Context
import androidx.room.Room
import com.concepts_and_quizzes.cds.data.english.repo.EnglishRepository
import com.concepts_and_quizzes.cds.data.english.repo.PyqpRepository
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.TopicStatDao
import com.concepts_and_quizzes.cds.data.analytics.db.QuestionStatDao
import com.concepts_and_quizzes.cds.data.analytics.db.SessionDao
import com.concepts_and_quizzes.cds.data.analytics.db.TimeAnalysisDao
import com.concepts_and_quizzes.cds.data.analytics.repo.AnalyticsRepository
import com.concepts_and_quizzes.cds.data.analytics.repo.TimeAnalysisRepository
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTraceDao
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportRepository
import com.concepts_and_quizzes.cds.data.analytics.db.SessionQuestionMapDao
import com.concepts_and_quizzes.cds.data.db.MIGRATION_8_9
import com.concepts_and_quizzes.cds.data.db.MIGRATION_9_10
import com.concepts_and_quizzes.cds.data.db.MIGRATION_10_11
import com.concepts_and_quizzes.cds.data.discover.db.ConceptDao
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
            .addMigrations(MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
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
    fun provideQuizTraceDao(db: EnglishDatabase): QuizTraceDao = db.quizTraceDao()

    @Provides
    fun provideSessionQuestionMapDao(db: EnglishDatabase): SessionQuestionMapDao = db.sessionQuestionMapDao()

    @Provides
    fun provideQuestionStatDao(db: EnglishDatabase): QuestionStatDao = db.questionStatDao()

    @Provides
    fun provideConceptDao(db: EnglishDatabase): ConceptDao = db.conceptDao()

    @Provides
    fun provideSessionDao(db: EnglishDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideTimeAnalysisDao(db: EnglishDatabase): TimeAnalysisDao = db.timeAnalysisDao()

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
        questionStatDao: QuestionStatDao
    ): PyqpRepository = PyqpRepository(pyqpDao, questionStatDao)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        attemptDao: AttemptLogDao,
        topicStatDao: TopicStatDao,
        questionStatDao: QuestionStatDao
    ): AnalyticsRepository =
        AnalyticsRepository(attemptDao, topicStatDao, questionStatDao)

    @Provides
    @Singleton
    fun provideTimeAnalysisRepository(
        sessionDao: SessionDao,
        timeAnalysisDao: TimeAnalysisDao
    ): TimeAnalysisRepository = TimeAnalysisRepository(sessionDao, timeAnalysisDao)

    @Provides
    @Singleton
    fun provideQuizReportRepository(
        traceDao: QuizTraceDao
    ): QuizReportRepository = QuizReportRepository(traceDao)

}
