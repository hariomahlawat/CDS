package com.concepts_and_quizzes.cds.ui.reports.time

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
import com.concepts_and_quizzes.cds.ui.skeleton.TimeSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class TimeViewModel @Inject constructor() : ViewModel()

@Composable
fun TimePage(
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.TIME,
        unlocked = false,
        progress = 0f,
        reason = LockedReason.TimeGate(5.hours)
    ),
    vm: TimeViewModel = hiltViewModel()
) {
    GhostOverlay(
        status = status,
        skeleton = { TimeSkeleton() },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Time")
        }
    }
}
