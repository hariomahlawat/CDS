package com.concepts_and_quizzes.cds.ui.reports.peer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.skeleton.PeerSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeerViewModel @Inject constructor() : ViewModel()

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
    GhostOverlay(
        status = status,
        skeleton = { PeerSkeleton() },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Peer")
        }
    }
}
