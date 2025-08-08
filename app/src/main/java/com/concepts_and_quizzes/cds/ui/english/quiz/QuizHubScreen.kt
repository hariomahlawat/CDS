package com.concepts_and_quizzes.cds.ui.english.quiz

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.components.CdsCard

@Composable
fun QuizHubScreen(nav: NavHostController, vm: QuizHubViewModel = hiltViewModel()) {
    val store by vm.store.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(store) {
        store?.let { s ->
            val result = snackbarHostState.showSnackbar(
                message = "Resume last test?",
                actionLabel = "Resume"
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
            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    listOf(
                        Mode("Full Paper", "Full past paper") { nav.navigate("english/pyqp?mode=FULL") },
                        Mode("Topic", "Topic drill") {
                            val topic = Uri.encode("t1")
                            nav.navigate("english/pyqp?mode=TOPIC&topic=$topic")
                        },
                        Mode("Wrongs", "Retry mistakes") { nav.navigate("english/pyqp?mode=WRONGS") },
                        Mode("Timed 20", "20Q sprint") { nav.navigate("english/pyqp?mode=TIMED20") },
                        Mode("Mixed", "Mixed bag") { nav.navigate("english/pyqp?mode=MIXED") }
                    )
                ) { m ->
                    ModeCard(m)
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

private data class Mode(val title: String, val caption: String, val action: () -> Unit)

@Composable
private fun ModeCard(mode: Mode) {
    CdsCard(
        modifier = Modifier
            .size(width = 160.dp, height = 100.dp)
            .clickable { mode.action() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(mode.title)
            Text(mode.caption)
        }
    }
}

