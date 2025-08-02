package com.concepts_and_quizzes.cds

import android.os.Bundle
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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val credentialManager by lazy { CredentialManager.create(this) }
    private lateinit var googleRequest: GetCredentialRequest
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUser = mutableStateOf<FirebaseUser?>(auth.currentUser)
    private val showRegister = mutableStateOf(false)
    private val prefs by lazy { getSharedPreferences("auth", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleRequest = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

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
                        onGoogleSignIn = ::startGoogleSignIn
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
        try {
            val result = credentialManager.getCredential(this@MainActivity, googleRequest)
            (result.credential as? GoogleIdTokenCredential)
                ?.let { firebaseAuthWithGoogle(it.idToken) }
        } catch (e: GetCredentialException) {
            // user cancelled or no credential – ignore
        }
    }

    private fun trySilentSignIn() = lifecycleScope.launch {
        try {
            val result = credentialManager.getCredential(this@MainActivity, googleRequest)
            (result.credential as? GoogleIdTokenCredential)
                ?.let { firebaseAuthWithGoogle(it.idToken) }
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            currentUser.value = auth.currentUser
        }
    }

    private fun cacheEmailPassword(email: String, password: String) {
        prefs.edit().putString("email", email).putString("password", password).apply()
        currentUser.value = auth.currentUser
    }

    private fun signOut() {
        auth.signOut()
        lifecycleScope.launch {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
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

