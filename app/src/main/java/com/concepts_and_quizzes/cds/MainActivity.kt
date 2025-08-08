package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.concepts_and_quizzes.cds.data.analytics.telemetry.Telemetry
import com.concepts_and_quizzes.cds.core.components.CdsBottomNavBar
import com.concepts_and_quizzes.cds.core.navigation.rootGraph
import com.concepts_and_quizzes.cds.core.theme.CDSTheme
import com.concepts_and_quizzes.cds.ui.onboarding.OnboardingScreen
import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import com.concepts_and_quizzes.cds.ui.nav.isAnalytics
import com.concepts_and_quizzes.cds.ui.nav.isConcepts
import com.concepts_and_quizzes.cds.ui.nav.isPyqp
import com.concepts_and_quizzes.cds.ui.nav.isReports
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var remoteConfig: RemoteConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setTheme(R.style.Theme_CDS)
        super.onCreate(savedInstanceState)
        Telemetry.logAppOpen()
        setContent { CDSApp(remoteConfig) }
    }
}

@Composable
private fun CDSApp(remoteConfig: RemoteConfig) {
    CDSTheme {
        val appVm: AppViewModel = hiltViewModel()
        val showOnboarding by appVm.showOnboarding.collectAsState()
        if (showOnboarding) {
            OnboardingScreen { appVm.completeOnboarding() }
        } else {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute == "english/dashboard" ||
                isConcepts(currentRoute) ||
                currentRoute == "quizHub" ||
                isPyqp(currentRoute) ||
                isAnalytics(currentRoute) ||
                isReports(currentRoute)

            Scaffold(bottomBar = { if (showBottomBar) CdsBottomNavBar(navController, remoteConfig) }) { padding ->
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
