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
            CdsCard {
                Column(
                    Modifier
                        .clickable { nav.navigate("english/pyqp") }
                        .padding(16.dp)
                ) {
                    Text("Start PYQ")
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
