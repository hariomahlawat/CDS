package com.concepts_and_quizzes.cds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.data.settings.UserPreferences

@HiltViewModel
class AppViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {
    val showOnboarding = prefs.onboardingDone
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun completeOnboarding() {
        viewModelScope.launch { prefs.setOnboardingDone() }
    }
}
