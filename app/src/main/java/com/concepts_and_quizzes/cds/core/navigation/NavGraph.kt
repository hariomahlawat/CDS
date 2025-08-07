package com.concepts_and_quizzes.cds.core.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.concepts_and_quizzes.cds.ui.reports.ReportsPagerScreen
import com.concepts_and_quizzes.cds.ui.english.pyqp.QuizScreen as PyqpQuizScreen

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
        if (mode == "WRONGS") {
            PyqpQuizScreen("", nav)
        } else {
            PyqpPaperListScreen(nav)
        }
    }
    composable("analytics") { AnalyticsCatalogueScreen(nav) }
    composable("analytics/trend") { PyqAnalyticsScreen(nav) }
    composable("analytics/heatmap") { PlaceholderAnalyticsScreen("Topic Heat-map", nav) }
    composable("analytics/peer") { PlaceholderAnalyticsScreen("Peer Percentile", nav) }
    composable("analytics/time") { PlaceholderAnalyticsScreen("Time Management", nav) }
    composable("reports") { ReportsPagerScreen() }
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
        report?.let { AnalysisScreen(it, vm.prefs) }
    }
}
