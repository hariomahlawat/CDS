package com.concepts_and_quizzes.cds.core.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {
    @Provides
    @Singleton
    fun provideRemoteConfig(): RemoteConfig = DefaultRemoteConfig()
}
