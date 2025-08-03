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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.concepts_and_quizzes.cds.auth.AuthViewModel
import com.concepts_and_quizzes.cds.auth.AuthViewModelFactory
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.currentUser == null) viewModel.trySilentSignIn()

        setContent {
            val currentUser = viewModel.currentUser
            val showRegister = viewModel.showRegister

            AnimatedContent(targetState = currentUser == null, label = "auth") { needsAuth ->
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
                    DashboardScreen(
                        name = currentUser?.displayName ?: "",
                        onSignOut = {
                            viewModel.signOut()
                            prefs.edit { clear() }
                        }
                    )
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

@Composable
fun DashboardScreen(name: String, onSignOut: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome_message, name),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onSignOut) {
                Text(text = stringResource(id = R.string.sign_out))
            }
        }
    }
}
