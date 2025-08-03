package com.concepts_and_quizzes.cds

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.concepts_and_quizzes.cds.auth.AuthRepository
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authRepository by lazy { AuthRepository(this) }
    private val currentUser = mutableStateOf<FirebaseUser?>(authRepository.currentUser)
    private val showRegister = mutableStateOf(false)
    private val signingIn = mutableStateOf(false)
    private val prefs by lazy { getSharedPreferences("auth", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (currentUser.value == null) trySilentSignIn()

        setContent {
            if (currentUser.value == null) {
                if (showRegister.value) {
                    RegisterScreen(
                        onRegistrationSuccess = { email, password ->
                            cacheEmailPassword(email, password)
                            showRegister.value = false
                        },
                        onBackToLogin = { showRegister.value = false }
                    )
                } else {
                    LoginScreen(
                        onNavigateToRegister = { showRegister.value = true },
                        onLoginSuccess = ::cacheEmailPassword,
                        onGoogleSignIn = ::startGoogleSignIn,
                        isSigningIn = signingIn.value
                    )
                }
            } else {
                DashboardScreen(
                    name = currentUser.value?.displayName ?: "",
                    onSignOut = ::signOut
                )
            }
        }
    }

    private fun startGoogleSignIn() = lifecycleScope.launch {
        signingIn.value = true
        try {
            authRepository.startGoogleSignIn()?.let { currentUser.value = it }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message ?: "Sign in failed", Toast.LENGTH_LONG).show()
        } finally {
            signingIn.value = false
        }
    }

    private fun trySilentSignIn() = lifecycleScope.launch {
        authRepository.trySilentSignIn()?.let { currentUser.value = it }
    }

    private fun cacheEmailPassword(email: String, password: String) {
        prefs.edit().putString("email", email).putString("password", password).apply()
        currentUser.value = authRepository.currentUser
    }

    private fun signOut() {
        lifecycleScope.launch {
            authRepository.signOut()
        }
        prefs.edit().clear().apply()
        currentUser.value = null
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
