package com.concepts_and_quizzes.cds.di

import android.content.Context
import com.concepts_and_quizzes.cds.data.repository.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionModule {

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        @ApplicationContext context: Context
    ): SubscriptionRepository = SubscriptionRepository(context)
}
