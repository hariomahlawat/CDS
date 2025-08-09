package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.ui.reports.heatmap.HeatmapPage
import com.concepts_and_quizzes.cds.ui.reports.time.TimePage

// Use your shared WindowRange helpers
import com.concepts_and_quizzes.cds.ui.reports.WindowRange
import com.concepts_and_quizzes.cds.ui.reports.label
import com.concepts_and_quizzes.cds.ui.reports.asWindowArg

// Keep existing navigation call working
@Composable
fun ReportsPagerScreen(navArgs: ReportsNavArgs) {
    ReportsScreen(
        analysisSessionId = navArgs.analysisSessionId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onShare: () -> Unit = {},
    startPage: Int = 0,
    analysisSessionId: String? = null
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var range by rememberSaveable { mutableStateOf(WindowRange.D7) }
    val windowArg = range.asWindowArg()

    var selectedTab by rememberSaveable { mutableIntStateOf(startPage) }
    val tabs = remember { listOf("Last", "Trend", "Heatmap", "Time", "Peer") }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Reports", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                actions = {
                    WindowPickerAction(range = range, onPick = { range = it })
                    IconButton(onClick = onShare) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Scrollable tabs so labels never wrap
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                when (selectedTab) {
                    0 -> LastQuizPage(sessionId = analysisSessionId)   // deep link supported
                    1 -> PlaceholderTab("Trend")                       // replace with TrendPage(windowArg)
                    2 -> HeatmapPage(window = windowArg)
                    3 -> TimePage(window = windowArg)
                    4 -> PlaceholderTab("Peer")                        // replace with PeerPage(windowArg)
                }
            }
        }
    }
}

/* ------------------------- Date-range picker in app bar ------------------------- */

@Composable
private fun WindowPickerAction(
    range: WindowRange,
    onPick: (WindowRange) -> Unit
) {
    var open by remember { mutableStateOf(false) }

    BadgedBox(
        badge = {
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(range.label(), style = MaterialTheme.typography.labelSmall)
            }
        }
    ) {
        IconButton(onClick = { open = true }) {
            Icon(Icons.Outlined.DateRange, contentDescription = "Date range: ${range.label()}")
        }
    }

    DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
        DropdownMenuItem(
            text = { Text("Last 7 days") },
            onClick = { open = false; onPick(WindowRange.D7) },
            trailingIcon = { if (range == WindowRange.D7) SelectedDot() }
        )
        DropdownMenuItem(
            text = { Text("Last 30 days") },
            onClick = { open = false; onPick(WindowRange.D30) },
            trailingIcon = { if (range == WindowRange.D30) SelectedDot() }
        )
        DropdownMenuItem(
            text = { Text("All time") },
            onClick = { open = false; onPick(WindowRange.ALL) },
            trailingIcon = { if (range == WindowRange.ALL) SelectedDot() }
        )
    }
}

@Composable
private fun SelectedDot() {
    Text("•", color = MaterialTheme.colorScheme.primary)
}

/* ------------------------------ Temporary stubs ----------------------------- */

@Composable
private fun PlaceholderTab(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name coming soon")
    }
}
