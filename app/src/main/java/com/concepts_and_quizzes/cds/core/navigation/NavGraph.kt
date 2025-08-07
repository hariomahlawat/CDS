package com.concepts_and_quizzes.cds.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptDetailScreen
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptsHomeScreen
import com.concepts_and_quizzes.cds.ui.english.dashboard.EnglishDashboardScreen
import com.concepts_and_quizzes.cds.ui.english.discover.DiscoverConceptDetailScreen
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizHubScreen
import com.concepts_and_quizzes.cds.ui.english.pyqp.PyqpPaperListScreen
import com.concepts_and_quizzes.cds.ui.english.pyqp.PyqAnalyticsScreen
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
    composable("analytics/pyq") { PyqAnalyticsScreen(nav) }
    composable(
        route = "english/pyqp/{paperId}",
        arguments = listOf(navArgument("paperId") { type = NavType.StringType })
    ) { back ->
        val pid = back.arguments?.getString("paperId") ?: return@composable
        PyqpQuizScreen(pid, nav)
    }
}
