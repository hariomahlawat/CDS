package com.concepts_and_quizzes.cds.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.concepts_and_quizzes.cds.data.analytics.availability.ModeAvailability
import com.concepts_and_quizzes.cds.data.analytics.availability.ModeAvailabilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ModeAvailabilityViewModel @Inject constructor(
    private val repo: ModeAvailabilityRepository
) : ViewModel() {
    private val _availability = MutableStateFlow<ModeAvailability?>(null)
    val availability: StateFlow<ModeAvailability?> = _availability

    init {
        viewModelScope.launch { _availability.value = repo.fetch() }
    }
}
