package com.concepts_and_quizzes.cds.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SubjectChooserViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val subscriptions: StateFlow<Map<String, Boolean>> =
        subscriptionRepository.subscriptions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
}
