package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisScreen

@Composable
fun ReportsPagerScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    startPage: Int = 0,
) {
    val pagerState = rememberPagerState(initialPage = startPage)
    VerticalPager(
        count = 5,
        state = pagerState,
        modifier = Modifier.testTag("reportsPager")
    ) { page ->
        when (page) {
            0 -> LastQuizPage(navArgs.analysisSessionId)
            1 -> TrendPagePlaceholder()
            2 -> HeatMapPlaceholder()
            3 -> TimeMgmtPlaceholder()
            4 -> PeerPlaceholder()
        }
    }
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

@Composable
fun TrendPagePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trend")
    }
}

@Composable
fun HeatMapPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Heat Map")
    }
}

@Composable
fun TimeMgmtPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Time Mgmt")
    }
}

@Composable
fun PeerPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Peer")
    }
}

