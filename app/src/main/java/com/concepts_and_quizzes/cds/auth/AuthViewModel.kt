package com.concepts_and_quizzes.cds.auth

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

// State holders
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null,
    val isLoading: Boolean = false
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    var loginState by androidx.compose.runtime.mutableStateOf(LoginUiState())
        private set
    var registerState by androidx.compose.runtime.mutableStateOf(RegisterUiState())
        private set
    var currentUser by androidx.compose.runtime.mutableStateOf<FirebaseUser?>(repository.currentUser)
        private set
    var showRegister by androidx.compose.runtime.mutableStateOf(false)
        private set

    fun onLoginEmailChange(value: String) {
        loginState = loginState.copy(
            email = value,
            emailError = if (isValidEmail(value)) null else "Invalid email"
        )
    }

    fun onLoginPasswordChange(value: String) {
        loginState = loginState.copy(
            password = value,
            passwordError = if (value.length >= 6) null else "Password too short"
        )
    }

    fun onRegisterNameChange(value: String) {
        registerState = registerState.copy(
            name = value,
            nameError = if (value.isNotBlank()) null else "Name required"
        )
    }

    fun onRegisterEmailChange(value: String) {
        registerState = registerState.copy(
            email = value,
            emailError = if (isValidEmail(value)) null else "Invalid email"
        )
    }

    fun onRegisterPasswordChange(value: String) {
        registerState = registerState.copy(
            password = value,
            passwordError = if (value.length >= 6) null else "Password too short"
        )
    }

    private fun isValidEmail(value: String) = Patterns.EMAIL_ADDRESS.matcher(value).matches()

    val isLoginValid: Boolean
        get() = loginState.emailError == null && loginState.passwordError == null &&
            loginState.email.isNotBlank() && loginState.password.isNotBlank()

    val isRegisterValid: Boolean
        get() = registerState.nameError == null && registerState.emailError == null &&
            registerState.passwordError == null && registerState.name.isNotBlank() &&
            registerState.email.isNotBlank() && registerState.password.isNotBlank()

    fun toggleForm() {
        showRegister = !showRegister
    }

    fun login(onSuccess: (FirebaseUser) -> Unit) {
        if (!isLoginValid) return
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, authError = null)
            try {
                repository.signInWithEmail(loginState.email, loginState.password)?.let {
                    currentUser = it
                    onSuccess(it)
                }
            } catch (e: Exception) {
                loginState = loginState.copy(authError = e.message)
            } finally {
                loginState = loginState.copy(isLoading = false)
            }
        }
    }

    fun register(onSuccess: (FirebaseUser) -> Unit) {
        if (!isRegisterValid) return
        viewModelScope.launch {
            registerState = registerState.copy(isLoading = true, authError = null)
            try {
                repository.registerWithEmail(
                    registerState.name,
                    registerState.email,
                    registerState.password
                )?.let {
                    currentUser = it
                    onSuccess(it)
                }
            } catch (e: Exception) {
                registerState = registerState.copy(authError = e.message)
            } finally {
                registerState = registerState.copy(isLoading = false)
            }
        }
    }

    fun startGoogleSignIn() {
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, authError = null)
            try {
                repository.startGoogleSignIn()?.let { currentUser = it }
            } catch (e: Exception) {
                loginState = loginState.copy(authError = e.message)
            } finally {
                loginState = loginState.copy(isLoading = false)
            }
        }
    }

    fun trySilentSignIn() {
        viewModelScope.launch {
            repository.trySilentSignIn()?.let { currentUser = it }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            currentUser = null
        }
    }
}

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

