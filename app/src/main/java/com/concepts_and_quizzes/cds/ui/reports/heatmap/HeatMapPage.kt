package com.concepts_and_quizzes.cds.ui.reports.heatmap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.ui.reports.GhostOverlay
import com.concepts_and_quizzes.cds.ui.skeleton.HeatmapSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HeatMapViewModel @Inject constructor() : ViewModel()

@Composable
fun HeatMapPage(
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.HEATMAP,
        unlocked = false,
        progress = 0f,
        reason = LockedReason.MoreQuizzes(5)
    ),
    vm: HeatMapViewModel = hiltViewModel()
) {
    GhostOverlay(
        status = status,
        skeleton = { HeatmapSkeleton() },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Heat Map")
        }
    }
}
