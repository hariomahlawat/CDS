package com.concepts_and_quizzes.cds.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    private val SHOW_CELEBRATIONS = booleanPreferencesKey("show_celebrations")

    val onboardingDone: Flow<Boolean> =
        context.userPrefsDataStore.data.map { it[ONBOARDING_DONE] ?: false }

    val showCelebrations: Flow<Boolean> =
        context.userPrefsDataStore.data.map { it[SHOW_CELEBRATIONS] ?: true }

    suspend fun setOnboardingDone() {
        context.userPrefsDataStore.edit { it[ONBOARDING_DONE] = true }
    }

    suspend fun setShowCelebrations(show: Boolean) {
        context.userPrefsDataStore.edit { it[SHOW_CELEBRATIONS] = show }
    }
}
