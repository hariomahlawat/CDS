package com.concepts_and_quizzes.cds.data.analytics.unlock

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsUnlockModule {
    @Provides
    @Singleton
    fun provideAnalyticsUnlockConfig(): AnalyticsUnlockConfig = AnalyticsUnlockConfig()
}
