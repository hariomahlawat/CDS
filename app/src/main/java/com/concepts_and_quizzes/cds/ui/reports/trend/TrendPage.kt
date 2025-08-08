package com.concepts_and_quizzes.cds.ui.reports.trend

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
import com.concepts_and_quizzes.cds.ui.skeleton.TrendSkeleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendViewModel @Inject constructor() : ViewModel()

@Composable
fun TrendPage(
    status: ModuleStatus = ModuleStatus(
        module = AnalyticsModule.TREND,
        unlocked = false,
        progress = 0f,
        reason = LockedReason.MoreQuizzes(3)
    ),
    vm: TrendViewModel = hiltViewModel()
) {
    GhostOverlay(
        status = status,
        skeleton = { TrendSkeleton() },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Trend")
        }
    }
}
