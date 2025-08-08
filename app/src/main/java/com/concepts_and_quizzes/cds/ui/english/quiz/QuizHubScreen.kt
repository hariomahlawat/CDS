package com.concepts_and_quizzes.cds.ui.english.quiz

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.core.components.CdsCard
import com.concepts_and_quizzes.cds.core.theme.Dimens
import com.concepts_and_quizzes.cds.ui.common.ModeCard

@Composable
fun QuizHubScreen(nav: NavHostController, vm: QuizHubViewModel = hiltViewModel()) {
    val store by vm.store.collectAsState()
    val availability = vm.availability.collectAsState().value
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
            availability?.let { avail ->
                ModeCard(
                    title = stringResource(R.string.wrong_only_title),
                    subtitle = stringResource(R.string.wrong_only_sub),
                    enabled = avail.wrongOnlyAvailable,
                    disabledCaption = stringResource(R.string.wrong_only_disabled)
                ) { nav.navigate("english/pyqp?mode=WRONGS") }
                ModeCard(
                    title = stringResource(R.string.timed20_title),
                    subtitle = stringResource(R.string.timed20_sub),
                    enabled = false
                ) { nav.navigate("comingSoon/timed20") }
                ModeCard(
                    title = stringResource(R.string.mixed_title),
                    subtitle = stringResource(R.string.coming_soon_title),
                    enabled = false
                ) { nav.navigate("comingSoon/mixed") }
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

