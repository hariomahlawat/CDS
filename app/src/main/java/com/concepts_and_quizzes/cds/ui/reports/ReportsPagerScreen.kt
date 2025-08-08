package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.concepts_and_quizzes.cds.ui.reports.trend.TrendPage
import com.concepts_and_quizzes.cds.ui.reports.heatmap.HeatMapPage
import com.concepts_and_quizzes.cds.ui.reports.time.TimePage
import com.concepts_and_quizzes.cds.ui.reports.peer.PeerPage
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportsPagerScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    startPage: Int = 0,
) {
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { 5 })
    VerticalPager(
        state = pagerState,
        modifier = Modifier.testTag("reportsPager")
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

@Composable
fun LastQuizPage(sessionId: String?) {
    val vm: LastQuizViewModel = hiltViewModel()
    LaunchedEffect(sessionId) { vm.load(sessionId) }
    val report by vm.report.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        report?.let { AnalysisScreen(it, vm.prefs) } ?: Text("No reports")
    }
}


