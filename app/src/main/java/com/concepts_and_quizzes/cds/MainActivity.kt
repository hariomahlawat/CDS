package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.concepts_and_quizzes.cds.auth.AuthViewModel
import com.concepts_and_quizzes.cds.auth.AuthViewModelFactory
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.concepts_and_quizzes.cds.core.theme.CDSTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.concepts_and_quizzes.cds.core.components.CdsBottomNavBar
import com.concepts_and_quizzes.cds.ui.concepts.ConceptsScreen
import com.concepts_and_quizzes.cds.ui.dashboard.GlobalDashboardScreen
import com.concepts_and_quizzes.cds.ui.english.EnglishScreen
import com.concepts_and_quizzes.cds.ui.subjectchooser.SubjectChooserScreen

class MainActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }
    private val masterKey by lazy {
        MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            this,
            "auth",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    private val skipAuthForTesting = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.currentUser == null) viewModel.trySilentSignIn()

        setContent {
            CDSTheme {
                val currentUser = viewModel.currentUser
                val showRegister = viewModel.showRegister
                val needsAuth = currentUser == null && !skipAuthForTesting

                AnimatedContent(targetState = needsAuth, label = "auth") { needsAuth ->
                    if (needsAuth) {
                        AnimatedContent(
                            targetState = showRegister,
                            transitionSpec = {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            },
                            label = "form"
                        ) { reg ->
                            if (reg) {
                                RegisterScreen(
                                    viewModel = viewModel,
                                    onRegistrationSuccess = { email, password ->
                                        cacheEmailPassword(email, password)
                                        viewModel.toggleForm()
                                    },
                                    onBackToLogin = { viewModel.toggleForm() }
                                )
                            } else {
                                LoginScreen(
                                    viewModel = viewModel,
                                    onNavigateToRegister = { viewModel.toggleForm() },
                                    onLoginSuccess = { email, password -> cacheEmailPassword(email, password) }
                                )
                            }
                        }
                    } else {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val showBottomBar = currentRoute != "subjectChooser"

                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) {
                                    CdsBottomNavBar(navController)
                                }
                            }
                        ) { padding ->
                            NavHost(
                                navController = navController,
                                startDestination = "subjectChooser",
                                modifier = Modifier.padding(padding)
                            ) {
                                composable("subjectChooser") { SubjectChooserScreen(navController) }
                                composable("dashboard") { GlobalDashboardScreen(navController) }
                                composable("concepts") { ConceptsScreen() }
                                composable("english") { EnglishScreen() }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cacheEmailPassword(email: String, password: String) {
        prefs.edit {
            putString("email", email)
            putString("password", password)
        }
    }
}
