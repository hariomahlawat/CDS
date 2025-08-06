package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.core.components.CdsBottomNavBar
import com.concepts_and_quizzes.cds.core.navigation.rootGraph
import com.concepts_and_quizzes.cds.core.theme.CDSTheme
import com.concepts_and_quizzes.cds.ui.onboarding.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CDSApp()
        }
    }
}

@Composable
private fun CDSApp() {
    CDSTheme {
        val appVm: AppViewModel = hiltViewModel()
        val showOnboarding by appVm.showOnboarding.collectAsState()
        if (showOnboarding) {
            OnboardingScreen { appVm.completeOnboarding() }
        } else {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val bottomBarRoutes = setOf(
                "english/dashboard",
                "english/concepts",
                "quizHub",
                "english/pyqp?mode={mode}&topic={topic}",
                "analytics/pyq"
            )
            val showBottomBar = currentRoute in bottomBarRoutes

            Scaffold(bottomBar = { if (showBottomBar) CdsBottomNavBar(navController) }) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = "english/dashboard",
                    modifier = Modifier.padding(padding)
                ) {
                    rootGraph(navController)
                }
            }
        }
    }
}
