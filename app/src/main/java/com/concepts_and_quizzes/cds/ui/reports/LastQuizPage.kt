package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisScreen

@Composable
fun LastQuizPage(sessionId: String?) {
    val vm: LastQuizViewModel = hiltViewModel()
    LaunchedEffect(sessionId) { vm.load(sessionId) }
    val report by vm.report.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        report?.let { AnalysisScreen(it, vm.prefs) } ?: EmptyState(title = "No reports")
    }
}
