package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUser = mutableStateOf<FirebaseUser?>(null)
    private val showRegister = mutableStateOf(false)
    private val prefs by lazy { getSharedPreferences("auth", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser.value = auth.currentUser

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Sign in failed, handle appropriately
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
                googleSignInClient.silentSignIn().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val account = task.result
                        firebaseAuthWithGoogle(account.idToken!!)
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
                        onGoogleSignIn = { signInLauncher.launch(googleSignInClient.signInIntent) }
                    )
                }
            } else {
                DashboardScreen(
                    user.displayName ?: "",
                    onSignOut = {
                        auth.signOut()
                        googleSignInClient.signOut()
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
