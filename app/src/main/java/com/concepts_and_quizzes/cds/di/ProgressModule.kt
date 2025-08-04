package com.concepts_and_quizzes.cds.di

import com.concepts_and_quizzes.cds.data.repository.ProgressRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProgressModule {
    @Provides
    @Singleton
    fun provideProgressRepository(): ProgressRepository = ProgressRepository()
}
