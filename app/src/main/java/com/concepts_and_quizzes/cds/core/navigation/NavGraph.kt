package com.concepts_and_quizzes.cds.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptDetailScreen
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptsHomeScreen
import com.concepts_and_quizzes.cds.ui.english.dashboard.EnglishDashboardScreen
import com.concepts_and_quizzes.cds.ui.english.discover.DiscoverConceptDetailScreen
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisScreen
import com.concepts_and_quizzes.cds.ui.english.analysis.AnalysisViewModel
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizHubScreen
import com.concepts_and_quizzes.cds.ui.english.pyqp.PyqpPaperListScreen
import com.concepts_and_quizzes.cds.ui.english.pyqp.PyqAnalyticsScreen
import com.concepts_and_quizzes.cds.ui.analytics.AnalyticsCatalogueScreen
import com.concepts_and_quizzes.cds.ui.analytics.PlaceholderAnalyticsScreen
import com.concepts_and_quizzes.cds.ui.reports.ReportsNavArgs
import com.concepts_and_quizzes.cds.ui.english.pyqp.QuizScreen as PyqpQuizScreen
import com.concepts_and_quizzes.cds.ui.common.ComingSoonScreen
import com.concepts_and_quizzes.cds.ui.common.ModeAvailabilityViewModel
import com.concepts_and_quizzes.cds.R
import com.concepts_and_quizzes.cds.ui.reports.ReportsPagerScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.rootGraph(nav: NavHostController) {
    composable("english/dashboard") { EnglishDashboardScreen(nav) }
    composable("english/concepts") { ConceptsHomeScreen(nav) }
    composable("english/concepts/{id}") { backStack ->
        val id = backStack.arguments?.getString("id") ?: return@composable
        ConceptDetailScreen(id, nav)
    }
    composable(
        route = "discover/{id}",
        arguments = listOf(navArgument("id") { type = NavType.IntType })
    ) {
        DiscoverConceptDetailScreen(nav)
    }
    composable("quizHub") { QuizHubScreen(nav) }
    composable(
        route = "english/pyqp?mode={mode}&topic={topic}",
        arguments = listOf(
            navArgument("mode") { type = NavType.StringType; defaultValue = "FULL" },
            navArgument("topic") { type = NavType.StringType; nullable = true }
        )
    ) { back ->
        val mode = back.arguments?.getString("mode") ?: "FULL"
        when (mode) {
            "WRONGS" -> {
                val vm: ModeAvailabilityViewModel = hiltViewModel()
                val avail by vm.availability.collectAsState()
                if (avail?.wrongOnlyAvailable == true) {
                    PyqpQuizScreen("", nav)
                } else if (avail != null) {
                    AlertDialog(
                        onDismissRequest = { nav.popBackStack() },
                        confirmButton = {
                            TextButton(onClick = { nav.popBackStack() }) {
                                Text("OK")
                            }
                        },
                        text = { Text(stringResource(R.string.wrong_only_disabled)) }
                    )
                }
            }
            "TIMED20" -> {
                LaunchedEffect(Unit) {
                    nav.popBackStack()
                    nav.navigate("comingSoon/timed20")
                }
            }
            "MIXED" -> {
                LaunchedEffect(Unit) {
                    nav.popBackStack()
                    nav.navigate("comingSoon/mixed")
                }
            }
            else -> {
                PyqpPaperListScreen(nav)
            }
        }
    }
    composable("comingSoon/{mode}") { backStackEntry ->
        val mode = backStackEntry.arguments?.getString("mode") ?: "feature"
        ComingSoonScreen(
            title = stringResource(R.string.coming_soon_title),
            body = when (mode) {
                "timed20" -> stringResource(R.string.coming_soon_body_timed20)
                "mixed" -> stringResource(R.string.coming_soon_body_mixed)
                else -> stringResource(R.string.coming_soon_body_generic)
            }
        )
    }
    composable("analytics") { AnalyticsCatalogueScreen(nav) }
    composable("analytics/trend") { PyqAnalyticsScreen(nav) }
    composable("analytics/heatmap") { PlaceholderAnalyticsScreen("Topic Heat-map", nav) }
    composable("analytics/peer") { PlaceholderAnalyticsScreen("Peer Percentile", nav) }
    composable("analytics/time") { PlaceholderAnalyticsScreen("Time Management", nav) }
    composable(
        route = "reports?analysisSessionId={analysisSessionId}&startPage={startPage}",
        arguments = listOf(
            navArgument("analysisSessionId") { type = NavType.StringType; nullable = true },
            navArgument("startPage") { type = NavType.IntType; defaultValue = 0 }
        )
    ) { backStack ->
        val sid = backStack.arguments?.getString("analysisSessionId")
        ReportsPagerScreen(navArgs = ReportsNavArgs(sid))
    }
    composable(
        route = "english/pyqp/{paperId}",
        arguments = listOf(navArgument("paperId") { type = NavType.StringType })
    ) { back ->
        val pid = back.arguments?.getString("paperId") ?: return@composable
        PyqpQuizScreen(pid, nav)
    }
    composable(
        route = "analysis/{sessionId}",
        arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
    ) {
        val vm: AnalysisViewModel = hiltViewModel()
        val report by vm.report.collectAsState()
        val weakest by vm.weakest.collectAsState()
        report?.let { AnalysisScreen(it, vm.prefs, weakest) }
    }
}
