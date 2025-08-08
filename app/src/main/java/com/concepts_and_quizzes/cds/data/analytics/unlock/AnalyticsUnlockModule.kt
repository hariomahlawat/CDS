package com.concepts_and_quizzes.cds.data.analytics.unlock

import com.concepts_and_quizzes.cds.core.config.RemoteConfig
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
    fun provideAnalyticsUnlockConfig(rc: RemoteConfig): AnalyticsUnlockConfig = AnalyticsUnlockConfig(rc)
}
