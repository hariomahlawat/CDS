package com.concepts_and_quizzes.cds.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.concepts_and_quizzes.cds.core.components.CdsBottomNavBar
import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import com.concepts_and_quizzes.cds.ui.nav.isAnalytics
import com.concepts_and_quizzes.cds.ui.nav.isConcepts
import com.concepts_and_quizzes.cds.ui.nav.isPyqp
import com.concepts_and_quizzes.cds.ui.nav.isReports
import org.junit.Rule
import org.junit.Test

class BottomBarUiTest {
    @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Composable
    private fun TestScaffold(route: String) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "english/dashboard") {
            composable("english/dashboard") {}
            composable("english/pyqp/{paperId}") {}
            composable("analysis/{sessionId}") {}
            composable("reports") {}
        }
        LaunchedEffect(route) { navController.navigate(route) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val rc = object : RemoteConfig { override fun getBoolean(key: String) = true }
        val showBottomBar = currentRoute == "english/dashboard" ||
            isConcepts(currentRoute) ||
            currentRoute == "quizHub" ||
            isPyqp(currentRoute) ||
            isAnalytics(currentRoute) ||
            isReports(currentRoute)

        Scaffold(bottomBar = { if (showBottomBar) CdsBottomNavBar(navController, rc) }) {}
    }

    @Test
    fun bottomBarVisibleOnParameterizedPyqp() {
        composeRule.setContent { TestScaffold("english/pyqp/1") }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Dashboard").assertExists()
    }

    @Test
    fun bottomBarHiddenOnAnalysisRoute() {
        composeRule.setContent { TestScaffold("analysis/123") }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Dashboard").assertDoesNotExist()
    }
}

