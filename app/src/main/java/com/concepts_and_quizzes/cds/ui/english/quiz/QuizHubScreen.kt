package com.concepts_and_quizzes.cds.ui.english.quiz

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.core.components.CdsCard
import com.concepts_and_quizzes.cds.core.theme.Dimens
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizHubScreen(nav: NavHostController, vm: QuizHubViewModel = hiltViewModel()) {
    val store by vm.store.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resumePrompt = stringResource(R.string.continue_last_quiz)
    val continueLabel = stringResource(R.string.continue_action)
    LaunchedEffect(store) {
        store?.let { s ->
            val result = snackbarHostState.showSnackbar(
                message = resumePrompt,
                actionLabel = continueLabel
            )
            if (result == SnackbarResult.ActionPerformed) {
                val dest = if (s.paperId.startsWith("WRONGS:")) {
                    val topic = Uri.encode(s.paperId.removePrefix("WRONGS:"))
                    "english/pyqp?mode=WRONGS&topic=$topic"
                } else {
                    "english/pyqp/${s.paperId}"
                }
                nav.navigate(dest) {
                    popUpTo("quizHub")
                }
                vm.restore(s.snapshot)
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Quiz Hub")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.ChipSpacingX),
                verticalArrangement = Arrangement.spacedBy(Dimens.ChipSpacingX)
            ) {
                val topic = Uri.encode("t1")
                ActionChip(Icons.Filled.Article, stringResource(R.string.quick_topic)) {
                    nav.navigate("english/pyqp?mode=TOPIC&topic=$topic")
                }
                ActionChip(Icons.Filled.Replay, stringResource(R.string.quick_wrong_only)) {
                    nav.navigate("english/pyqp?mode=WRONGS")
                }
                ActionChip(Icons.Filled.Timer, stringResource(R.string.quick_timed_20)) {
                    nav.navigate("english/pyqp?mode=TIMED20")
                }
                ActionChip(Icons.Filled.Shuffle, stringResource(R.string.quick_mixed)) {
                    nav.navigate("english/pyqp?mode=MIXED")
                }
            }
            CdsCard {
                Column(
                    Modifier
                        .clickable { nav.navigate("analytics") }
                        .padding(16.dp)
                ) {
                    Text("Analytics")
                }
            }
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

