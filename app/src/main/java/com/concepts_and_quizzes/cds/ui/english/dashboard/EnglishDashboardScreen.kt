package com.concepts_and_quizzes.cds.ui.english.dashboard

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import java.time.LocalTime
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ProgressIndicatorDefaults
import com.concepts_and_quizzes.cds.core.components.CdsCard
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.core.theme.Dimens
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizHubViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun EnglishDashboardScreen(nav: NavHostController, vm: EnglishDashboardViewModel = hiltViewModel()) {
    val resumeVm: QuizHubViewModel = hiltViewModel()
    val resume by resumeVm.store.collectAsState()
    val savedProgress by resumeVm.progress.collectAsState()
    val summary by vm.summary.collectAsState()
    val questionsToday by vm.questionsToday.collectAsState()
    val concepts by vm.tips.collectAsState()
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
    Column(Modifier.padding(padding)) {
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

          FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ChipSpacingX),
            verticalArrangement = Arrangement.spacedBy(Dimens.ChipSpacingX)
        ) {
            ActionChip(Icons.Filled.AutoStories, "Concepts") { nav.navigate("english/concepts") }
            ActionChip(Icons.Filled.School, "Mock Tests") { nav.navigate("quizHub") }
            ActionChip(Icons.AutoMirrored.Filled.MenuBook, "Past Papers") { nav.navigate("english/pyqp") }
        }

        resume?.let { s ->
            val prog = savedProgress
            CdsCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable {
                        val dest = if (s.paperId.startsWith("WRONGS:")) {
                            val topic = Uri.encode(s.paperId.removePrefix("WRONGS:"))
                            "english/pyqp?mode=WRONGS&topic=$topic"
                        } else {
                            "english/pyqp/${s.paperId}"
                        }
                        scope.launch { snackbarHostState.showSnackbar("Resumed") }
                        nav.navigate(dest)
                        resumeVm.restore(s.snapshot)
                    }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Continue last quiz")
                    prog?.let { Text("${s.paperId} - ${it.percent}%") }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MiniTrendCard(
                modifier = Modifier.weight(1f)
            ) { nav.navigate("reports?startPage=1") }
            WeakestTopicCard(
                modifier = Modifier.weight(1f)
            ) { nav.navigate("reports?startPage=0") }
        }

        Row(
            Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.QuestionAnswer, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("$count Questions practised today")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Discover",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        DiscoverCarousel(concepts, vm, nav)
    }
    }
}

@Composable
private fun ActionChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f, label = "scale")

    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(label)
        }
    }
}

@Composable
private fun MiniTrendCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    CdsCard(modifier = modifier, onClick = onClick) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Trend")
        }
    }
}

@Composable
private fun WeakestTopicCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    CdsCard(modifier = modifier, onClick = onClick) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Weakest Topic")
        }
    }
}

