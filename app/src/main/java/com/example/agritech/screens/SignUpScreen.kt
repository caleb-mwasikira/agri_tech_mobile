package com.example.agritech.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritech.MainActivity
import com.example.agritech.data.AuthViewModel
import com.example.agritech.data.Error
import com.example.agritech.data.Route
import com.example.agritech.ui.theme.AgriTechTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = viewModel(),
    navigateTo: (Route) -> Unit,
) {
    val context = LocalContext.current
    val invalidFields = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        authViewModel.errors.collectLatest { error ->
            when (error) {
                is Error.BadResponse -> {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }

                is Error.ValidationException -> {
                    invalidFields.add(error.field)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome to AgriTech",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Create an account today",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = authViewModel.username,
                onValueChange = {
                    authViewModel.username = it
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = invalidFields.contains("username")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = authViewModel.email,
                onValueChange = {
                    authViewModel.email = it
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = invalidFields.contains("email")
            )

            Spacer(modifier = Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = authViewModel.password,
                onValueChange = {
                    authViewModel.password = it
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                isError = invalidFields.contains("password")
            )

            Spacer(modifier = Modifier.height(12.dp))

            var confirmPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = authViewModel.confirmPassword,
                onValueChange = {
                    authViewModel.confirmPassword = it
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(
                        onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = "Toggle Confirm Password Visibility"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                isError = invalidFields.contains("confirmPassword")
            )

            Spacer(modifier = Modifier.height(24.dp))

            val scope = rememberCoroutineScope()
            val sharedPreferences = remember {
                context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val signUpSuccess = authViewModel.createAccount()
                            if (signUpSuccess) {
                                sharedPreferences.edit {
                                    putString(MainActivity.USERNAME, authViewModel.username)
                                    putString(MainActivity.EMAIL, authViewModel.email)
                                }

                                navigateTo(Route.LoginScreen)
                                return@launch
                            }
                        }
                    },
                    shape = RoundedCornerShape(50), // Makes it oval
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }

                TextButton(
                    onClick = {
                        navigateTo(Route.LoginScreen)
                    }
                ) {
                    Text(
                        "Already have an account? Login",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    AgriTechTheme {
        SignUpScreen(
            navigateTo = {},
        )
    }
}