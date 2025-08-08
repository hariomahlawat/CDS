package com.concepts_and_quizzes.cds.ui.english.dashboard

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import java.time.LocalTime
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizHubViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnglishDashboardScreen(nav: NavHostController, vm: EnglishDashboardViewModel = hiltViewModel()) {
    val resumeVm: QuizHubViewModel = hiltViewModel()
    val resume by resumeVm.store.collectAsState()
    val savedProgress by resumeVm.progress.collectAsState()
    val summary by vm.summary.collectAsState()
    val questionsToday by vm.questionsToday.collectAsState()
    val availability = vm.availability.collectAsState().value
    val tipsState by vm.tips.collectAsState()
    val count by animateIntAsState(targetValue = questionsToday, label = "count")
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
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .graphicsLayer {
                            clip = true
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            shadowElevation = 8f
                        }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            greeting,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        val best by remember(summary) { derivedStateOf { summary.best / 100f } }
                        CircularProgressIndicator(
                            progress = { best },
                            modifier = Modifier.size(48.dp),
                            color = ProgressIndicatorDefaults.circularColor,
                            strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                        )
                    }
                }
            }

            resume?.let { s ->
                val prog = savedProgress
                item {
                    DashboardTile(
                        title = stringResource(R.string.continue_quiz),
                        subtitle = prog?.let { "${it.percent}%" },
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
                    ) {
                        prog?.let { p ->
                            LinearProgressIndicator(
                                progress = p.percent / 100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

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
                    onClick = { nav.navigate("reports?startPage=1") }
                )
            }

            item {
                DashboardTile(
                    title = "Weakest Topic",
                    onClick = { nav.navigate("reports?startPage=0") }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.QuestionAnswer, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("$count Questions practised today")
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Discover",
                    style = MaterialTheme.typography.titleMedium
                )
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
