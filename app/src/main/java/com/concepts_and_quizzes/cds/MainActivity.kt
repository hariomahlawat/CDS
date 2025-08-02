package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetGoogleIdTokenCredentialOption
import androidx.credentials.GoogleIdTokenCredential
import androidx.credentials.ui.CredentialManagerContract
import androidx.lifecycle.lifecycleScope
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val credManager by lazy { CredentialManager.create(this) }
    private lateinit var request: GetCredentialRequest
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUser = mutableStateOf<FirebaseUser?>(null)
    private val showRegister = mutableStateOf(false)
    private val prefs by lazy { getSharedPreferences("auth", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser.value = auth.currentUser

        request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdTokenCredentialOption(
                    serverClientId = getString(R.string.default_web_client_id)
                )
            )
            .build()

        val signInLauncher = registerForActivityResult(CredentialManagerContract()) { result ->
            when (result) {
                is GetCredentialResponse -> {
                    val cred = result.credential as GoogleIdTokenCredential
                    firebaseAuthWithGoogle(cred.idToken)
                }
                else -> {
                    // Sign in failed or cancelled
                }
            }
        }

        if (currentUser.value == null) {
            val email = prefs.getString("email", null)
            val password = prefs.getString("password", null)
            if (email != null && password != null) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) currentUser.value = auth.currentUser
                    }
            } else {
                lifecycleScope.launch {
                    try {
                        val response = credManager.getCredential(request, this@MainActivity)
                        val cred = response.credential as GoogleIdTokenCredential
                        firebaseAuthWithGoogle(cred.idToken)
                    } catch (_: Exception) {
                        // No credential available or sign in failed silently
                    }
                }
            }
        }

        setContent {
            val user = currentUser.value
            if (user == null) {
                if (showRegister.value) {
                    RegisterScreen(
                        onRegistrationSuccess = { email, password ->
                            prefs.edit().putString("email", email).putString("password", password).apply()
                            currentUser.value = auth.currentUser
                            showRegister.value = false
                        },
                        onBackToLogin = { showRegister.value = false }
                    )
                } else {
                    LoginScreen(
                        onNavigateToRegister = { showRegister.value = true },
                        onLoginSuccess = { email, password ->
                            prefs.edit().putString("email", email).putString("password", password).apply()
                            currentUser.value = auth.currentUser
                        },
                        onGoogleSignIn = { signInLauncher.launch(request) }
                    )
                }
            } else {
                DashboardScreen(
                    user.displayName ?: "",
                    onSignOut = {
                        auth.signOut()
                        lifecycleScope.launch {
                            credManager.clearCredentialState(ClearCredentialStateRequest())
                        }
                        prefs.edit().clear().apply()
                        currentUser.value = null
                    }
                )
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                currentUser.value = auth.currentUser
            }
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
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onSignOut) {
                Text(text = stringResource(id = R.string.sign_out))
            }
        }
    }
}
