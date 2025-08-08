package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import android.graphics.Rect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.ui.reports.trend.TrendPage
import com.concepts_and_quizzes.cds.ui.reports.heatmap.HeatMapPage
import com.concepts_and_quizzes.cds.ui.reports.time.TimePage
import com.concepts_and_quizzes.cds.ui.reports.peer.PeerPage
import com.concepts_and_quizzes.cds.util.ShareUtils

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    shared: ReportsSharedViewModel = hiltViewModel()
) {
    val window by shared.window.collectAsState()
    val pagerState = rememberPagerState(initialPage = shared.startPage, pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
    var pagerRect by remember { mutableStateOf<Rect?>(null) }
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val tabs = remember { listOf("Last", "Trend", "Heatmap", "Time", "Peer") }
    val windows = remember { Window.entries }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                actions = {
                    IconButton(onClick = {
                        pagerRect?.let { rect ->
                            ShareUtils.shareViewAsImage(
                                context = context,
                                view = view,
                                rect = rect
                            )
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { inner ->
        Column(modifier = Modifier.padding(inner)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                windows.forEach { w ->
                    FilterChip(
                        selected = window == w,
                        onClick = { shared.setWindow(w) },
                        label = { Text(text = w.label) }
                    )
                }
            }
            TabRow(selectedTabIndex = currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentPage == index,
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
                    .onGloballyPositioned { coords ->
                        val b = coords.boundsInWindow()
                        pagerRect = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
                    }
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
}

@Composable
fun ReportsPagerScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    startPage: Int = 0
) {
    ReportsScreen(navArgs = navArgs)
}
