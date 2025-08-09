package com.concepts_and_quizzes.cds.ui.english.dashboard

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizHubViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnglishDashboardScreen(
    nav: NavHostController,
    vm: EnglishDashboardViewModel = hiltViewModel()
) {
    val resumeVm: QuizHubViewModel = hiltViewModel()
    val resume by resumeVm.store.collectAsState()
    val savedProgress by resumeVm.progress.collectAsState()

    val summary by vm.summary.collectAsState()
    val questionsToday by vm.questionsToday.collectAsState()
    val availability = vm.availability.collectAsState().value
    val tipsState by vm.tips.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val greeting = remember {
        val hour = LocalTime.now().hour
        val part = when (hour) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            else -> "Evening"
        }
        "$part revision, Magnus!"
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* ---------- HERO HEADER ---------- */
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Box(
                        Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .graphicsLayer { clip = true; shape = RoundedCornerShape(24.dp) }
                            .padding(horizontal = 18.dp)
                            .fillMaxSize()
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)) {
                                    Icon(
                                        Icons.Filled.QuestionAnswer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    greeting,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            val best by remember(summary) { derivedStateOf { summary.best / 100f } }
                            CircularProgressIndicator(
                                progress = { best },
                                modifier = Modifier.size(52.dp),
                                color = ProgressIndicatorDefaults.circularColor,
                                strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                            )
                        }
                    }
                }
            }

            /* ---------- KPI PILLS ---------- */
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KpiPill(
                        label = "Practised today",
                        value = questionsToday.toString(),
                        modifier = Modifier.weight(1f)     // ✅ weight at call-site
                    )
                    KpiPill(
                        label = "Best score",
                        value = "${summary.best}%",
                        modifier = Modifier.weight(1f)
                    )
                    KpiPill(
                        label = "Papers",
                        value = summary.papers.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            /* ---------- CONTINUE CARD ---------- */
            resume?.let { s ->
                val prog = savedProgress
                item {
                    DashboardTile(
                        title = stringResource(R.string.continue_quiz),
                        subtitle = prog?.let { "${it.percent}%" },
                        content = {
                            prog?.let { p ->
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { p.percent / 100f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = ProgressIndicatorDefaults.linearColor,
                                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                                )
                            }
                        },
                        onClick = {
                            val dest = if (s.paperId.startsWith("WRONGS:")) {
                                val topic = s.paperId.removePrefix("WRONGS:")
                                if (topic.isNotEmpty()) {
                                    val enc = Uri.encode(topic)
                                    "english/pyqp?mode=WRONGS&topic=$enc"
                                } else {
                                    "english/pyqp?mode=WRONGS"
                                }
                            } else {
                                "english/pyqp/${s.paperId}"
                            }
                            scope.launch { snackbarHostState.showSnackbar("Resumed") }
                            nav.navigate(dest)
                        }
                    )
                }
            }

            /* ---------- FEATURE TILES ---------- */
            availability?.let { avail ->
                item {
                    DashboardTile(
                        title = stringResource(R.string.pyqp_title),
                        subtitle = stringResource(R.string.pyqp_sub),
                        onClick = { nav.navigate("english/pyqp") }
                    )
                }
                item {
                    DashboardTile(
                        title = stringResource(R.string.wrong_only_title),
                        subtitle = stringResource(R.string.wrong_only_sub),
                        enabled = avail.wrongOnlyAvailable,
                        onClick = { nav.navigate("english/pyqp?mode=WRONGS") }
                    )
                }
                item {
                    DashboardTile(
                        title = stringResource(R.string.timed20_title),
                        subtitle = stringResource(R.string.timed20_sub),
                        enabled = false,
                        onClick = { nav.navigate("comingSoon/timed20") }
                    )
                }
                item {
                    DashboardTile(
                        title = stringResource(R.string.mixed_title),
                        subtitle = stringResource(R.string.coming_soon_title),
                        enabled = false,
                        onClick = { nav.navigate("comingSoon/mixed") }
                    )
                }
            }

            item {
                DashboardTile(
                    title = "Trend",
                    subtitle = "How you are improving",
                    onClick = { nav.navigate("reports?startPage=1") }
                )
            }

            item {
                DashboardTile(
                    title = "Weakest Topic",
                    subtitle = "Target your gaps",
                    onClick = { nav.navigate("reports?startPage=0") }
                )
            }

            /* ---------- FOOTER & DISCOVER ---------- */
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.QuestionAnswer, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("${questionsToday} Questions practised today")
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) { HorizontalDivider() }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Discover", style = MaterialTheme.typography.titleMedium)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                when (val s = tipsState) {
                    UiState.Loading -> LoadingSkeleton()
                    is UiState.Error -> ErrorState(s.message) { vm.refreshTips() }
                    is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refreshTips() }
                    is UiState.Data -> DiscoverCarousel(s.value, vm, nav)
                }
            }
        }
    }
}

/* ---------- Local building block: KPI pill ---------- */

@Composable
private fun KpiPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier                   // weight comes from call-site
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Spacer(Modifier.size(8.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
