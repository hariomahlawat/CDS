package com.concepts_and_quizzes.cds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.concepts_and_quizzes.cds.ui.theme.CDSTheme
import com.concepts_and_quizzes.cds.auth.LoginScreen
import com.concepts_and_quizzes.cds.auth.RegisterScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            CDSTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuthApp()
                }
            }
        }
    }
}

private sealed class Screen {
    data object Login : Screen()
    data object Register : Screen()
    data object Home : Screen()
}

@Composable
private fun AuthApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Login) }
    when (screen) {
        Screen.Login -> LoginScreen(
            onNavigateToRegister = { screen = Screen.Register },
            onLoginSuccess = { screen = Screen.Home }
        )
        Screen.Register -> RegisterScreen(
            onRegistrationSuccess = { screen = Screen.Home },
            onBackToLogin = { screen = Screen.Login }
        )
        Screen.Home -> HomeScreen()
    }
}

@Composable
private fun HomeScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    Text("Welcome ${user?.email ?: ""}")
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    CDSTheme {
        AuthApp()
    }
}