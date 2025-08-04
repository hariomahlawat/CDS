package com.concepts_and_quizzes.cds.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptDetailScreen
import com.concepts_and_quizzes.cds.ui.english.concepts.ConceptsHomeScreen
import com.concepts_and_quizzes.cds.ui.english.dashboard.EnglishDashboardScreen
import com.concepts_and_quizzes.cds.ui.english.quiz.QuizScreen

fun NavGraphBuilder.rootGraph(nav: NavHostController) {
    composable("english/dashboard") { EnglishDashboardScreen(nav) }
    composable("english/concepts") { ConceptsHomeScreen(nav) }
    composable("english/concepts/{id}") { backStack ->
        val id = backStack.arguments?.getString("id") ?: return@composable
        ConceptDetailScreen(id, nav)
    }
    composable("english/quiz/{topicId}") { backStack ->
        val topicId = backStack.arguments?.getString("topicId") ?: return@composable
        QuizScreen(topicId, nav)
    }
}
