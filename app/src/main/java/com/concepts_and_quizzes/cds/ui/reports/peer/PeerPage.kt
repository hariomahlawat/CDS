package com.concepts_and_quizzes.cds.ui.reports.peer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.skeleton.PeerSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class PeerViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val state: StateFlow<UiState<Unit>> = _state

    init { refresh() }

    fun refresh() {
        _state.value = UiState.Empty("No peer data", "Reload")
    }
}

@Composable
fun PeerPage(
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.PEER,
        unlocked = false,
        progress = 0f,
        reason = null
    ),
    vm: PeerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    GhostOverlay(
        status = status,
        skeleton = { PeerSkeleton() },
    ) {
        when (val s = state) {
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh() }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
            is UiState.Data -> Box(Modifier.fillMaxSize()) { }
        }
    }
}
