package com.concepts_and_quizzes.cds.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.subscriptionDataStore: DataStore<Preferences> by preferencesDataStore(name = "subscriptions")

class SubscriptionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.subscriptionDataStore

    private val englishKey = booleanPreferencesKey("premium_english")
    private val mathKey = booleanPreferencesKey("premium_math")
    private val gkKey = booleanPreferencesKey("premium_gk")

    val subscriptions: Flow<Map<String, Boolean>> = dataStore.data.map { prefs ->
        mapOf(
            "premium_english" to (prefs[englishKey] ?: false),
            "premium_math" to (prefs[mathKey] ?: false),
            "premium_gk" to (prefs[gkKey] ?: false)
        )
    }
}
