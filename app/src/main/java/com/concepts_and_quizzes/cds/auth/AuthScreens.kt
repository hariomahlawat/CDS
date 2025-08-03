package com.concepts_and_quizzes.cds.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String, String) -> Unit,
) {
    val state = viewModel.loginState
    val isValid = viewModel.isLoginValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.startGoogleSignIn() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text(stringResource(id = R.string.sign_in_with_google))
                }
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onLoginEmailChange,
                    label = { Text("Email") },
                    isError = state.emailError != null,
                    supportingText = { state.emailError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onLoginPasswordChange,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = state.passwordError != null,
                    supportingText = { state.passwordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.login { onLoginSuccess(state.email, state.password) }
                    })
                )
                state.authError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { viewModel.login { onLoginSuccess(state.email, state.password) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValid && !state.isLoading
                ) {
                    Text("Login")
                }
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Need an account? Register")
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegistrationSuccess: (String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val state = viewModel.registerState
    val isValid = viewModel.isRegisterValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onRegisterNameChange,
                    label = { Text("Name") },
                    isError = state.nameError != null,
                    supportingText = { state.nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onRegisterEmailChange,
                    label = { Text("Email") },
                    isError = state.emailError != null,
                    supportingText = { state.emailError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onRegisterPasswordChange,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = state.passwordError != null,
                    supportingText = { state.passwordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.register { onRegistrationSuccess(state.email, state.password) }
                    })
                )
                state.authError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { viewModel.register { onRegistrationSuccess(state.email, state.password) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValid && !state.isLoading
                ) {
                    Text("Register")
                }
                TextButton(
                    onClick = onBackToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Login")
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
