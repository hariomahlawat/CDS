package com.concepts_and_quizzes.cds.ui.reports

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarViewMonth
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.ui.reports.heatmap.HeatmapPage
import com.concepts_and_quizzes.cds.ui.reports.time.TimePage

// shared range helpers
import com.concepts_and_quizzes.cds.ui.reports.WindowRange
import com.concepts_and_quizzes.cds.ui.reports.label
import com.concepts_and_quizzes.cds.ui.reports.asWindowArg
import com.concepts_and_quizzes.cds.ui.reports.trend.TrendPage

/* ---- keep existing navigation API ---- */

@Composable
fun ReportsPagerScreen(
    navArgs: ReportsNavArgs = ReportsNavArgs(),
    onStartPractice: (() -> Unit)? = null,
) {
    ReportsScreen(
        analysisSessionId = navArgs.analysisSessionId,
        onStartPractice = onStartPractice,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onShare: () -> Unit = {},
    startPage: Int = 0,
    analysisSessionId: String? = null,
    onStartPractice: (() -> Unit)? = null,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var range by rememberSaveable { mutableStateOf(WindowRange.D7) }
    val windowArg = range.asWindowArg()

    var selectedTab by rememberSaveable { mutableIntStateOf(startPage) }

    // Tab model: label + outlined/filled icons
    data class TabSpec(
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val iconSelected: androidx.compose.ui.graphics.vector.ImageVector
    )

    val tabs = remember {
        listOf(
            TabSpec("Last",     Icons.Outlined.History,          Icons.Filled.History),
            TabSpec("Trend", Icons.AutoMirrored.Outlined.TrendingUp,
                Icons.AutoMirrored.Filled.TrendingUp
            ),
            TabSpec("Heatmap",  Icons.Outlined.CalendarViewMonth,Icons.Filled.CalendarViewMonth),
            TabSpec("Time",     Icons.Outlined.AccessTime,       Icons.Filled.AccessTime),
            TabSpec("Peer",     Icons.Outlined.Groups,           Icons.Filled.Groups)
        )
    }

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
            // Icon-only tabs (scrollable)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 6.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, spec ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) spec.iconSelected else spec.icon,
                                contentDescription = spec.label
                            )
                        },
                        modifier = Modifier.semantics { contentDescription = spec.label }
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                when (selectedTab) {
                    0 -> LastQuizPage(sessionId = analysisSessionId)
                    1 -> TrendPage(window = windowArg, onStartPractice = onStartPractice)
                    2 -> HeatmapPage(window = windowArg)
                    3 -> TimePage(window = windowArg)
                    4 -> PlaceholderTab("Peer")          // replace with PeerPage(window = windowArg)
                }
            }
        }
    }
}

/* ------------------------- date range in app bar ------------------------- */

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
        DropdownMenuItem(text = { Text("Last 7 days") },
            onClick = { open = false; onPick(WindowRange.D7) },
            trailingIcon = { if (range == WindowRange.D7) SelectedDot() })
        DropdownMenuItem(text = { Text("Last 30 days") },
            onClick = { open = false; onPick(WindowRange.D30) },
            trailingIcon = { if (range == WindowRange.D30) SelectedDot() })
        DropdownMenuItem(text = { Text("All time") },
            onClick = { open = false; onPick(WindowRange.ALL) },
            trailingIcon = { if (range == WindowRange.ALL) SelectedDot() })
    }
}

@Composable private fun SelectedDot() {
    Text("•", color = MaterialTheme.colorScheme.primary)
}

/* ------------------------------ temporary stubs ----------------------------- */

@Composable
private fun PlaceholderTab(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name coming soon")
    }
}
