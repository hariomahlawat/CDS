package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun ReportsPagerScreen(startPage: Int = 0) {
    val pagerState = rememberPagerState(initialPage = startPage)
    VerticalPager(
        count = 5,
        state = pagerState,
        modifier = Modifier.testTag("reportsPager")
    ) { page ->
        when (page) {
            0 -> LastQuizPage()
            1 -> TrendPagePlaceholder()
            2 -> HeatMapPlaceholder()
            3 -> TimeMgmtPlaceholder()
            4 -> PeerPlaceholder()
        }
    }
}

@Composable
fun LastQuizPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Last Quiz")
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

