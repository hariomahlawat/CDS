package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.ui.reports.trend.TrendPage
import com.concepts_and_quizzes.cds.ui.reports.heatmap.HeatMapPage
import com.concepts_and_quizzes.cds.ui.reports.time.TimePage
import com.concepts_and_quizzes.cds.ui.reports.peer.PeerPage
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportsScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    shared: ReportsSharedViewModel = hiltViewModel()
) {
    val window by shared.window.collectAsState()
    val pagerState = rememberPagerState(initialPage = shared.startPage, pageCount = { 5 })
    val scope = rememberCoroutineScope()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Window.values().forEach { w ->
                FilterChip(
                    selected = window == w,
                    onClick = { shared.setWindow(w) },
                    label = { Text(text = w.label) }
                )
            }
        }
        val tabs = listOf("Last", "Trend", "Heatmap", "Time", "Peer")
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .testTag("reportsPager")
        ) { page ->
            when (page) {
                0 -> LastQuizPage(navArgs.analysisSessionId)
                1 -> TrendPage()
                2 -> HeatMapPage()
                3 -> TimePage()
                4 -> PeerPage()
            }
        }
    }
}

@Composable
fun ReportsPagerScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    startPage: Int = 0
) {
    ReportsScreen(navArgs = navArgs)
}

@Composable
fun LastQuizPage(sessionId: String?) {
    val vm: LastQuizViewModel = hiltViewModel()
    LaunchedEffect(sessionId) { vm.load(sessionId) }
    val report by vm.report.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        report?.let { AnalysisScreen(it, vm.prefs) } ?: Text("No reports")
    }
}
