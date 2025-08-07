package com.concepts_and_quizzes.cds.ui.analytics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason
import com.concepts_and_quizzes.cds.data.analytics.unlock.UnlockStats
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsUnlocker
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnalyticsCatalogue(statuses: List<ModuleStatus>, nav: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(statuses) { s ->
            val alpha = if (s.unlocked) 1f else 0.3f
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha)
                    .clickable(enabled = s.unlocked) {
                        nav.navigate("analytics/${s.module.name.lowercase()}")
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(imageVector = s.module.icon(), contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text(s.module.displayName(), style = MaterialTheme.typography.titleMedium)
                    if (!s.unlocked && s.reason != null) {
                        Text(
                            when (s.reason) {
                                is LockedReason.MoreQuizzes -> "Complete ${s.reason.remaining} more quizzes"
                                is LockedReason.TimeGate -> "Opens in ${s.reason.duration.inWholeHours} h"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

fun AnalyticsModule.icon() = when (this) {
    AnalyticsModule.TREND -> Icons.Filled.TrendingUp
    AnalyticsModule.HEATMAP -> Icons.Filled.GridOn
    AnalyticsModule.PEER -> Icons.Filled.People
    AnalyticsModule.TIME -> Icons.Filled.AccessTime
}

fun AnalyticsModule.displayName() = when (this) {
    AnalyticsModule.TREND -> "Performance Trend"
    AnalyticsModule.HEATMAP -> "Topic Heat-map"
    AnalyticsModule.PEER -> "Peer Percentile"
    AnalyticsModule.TIME -> "Time Management"
}

@HiltViewModel
class AnalyticsCatalogueViewModel @Inject constructor(
    private val unlocker: AnalyticsUnlocker
) : ViewModel() {
    private val _statuses = MutableStateFlow<List<ModuleStatus>>(emptyList())
    val statuses: StateFlow<List<ModuleStatus>> = _statuses

    init {
        refresh(UnlockStats(0, 0, false, 0L))
    }

    fun refresh(stats: UnlockStats) {
        _statuses.value = unlocker.statuses(stats)
    }
}

@Composable
fun AnalyticsCatalogueScreen(nav: NavController, vm: AnalyticsCatalogueViewModel = hiltViewModel()) {
    val statuses by vm.statuses.collectAsState()
    AnalyticsCatalogue(statuses, nav)
}

@Composable
fun PlaceholderAnalyticsScreen(label: String, nav: NavController) {
    androidx.compose.material3.Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(label)
        }
    }
}
