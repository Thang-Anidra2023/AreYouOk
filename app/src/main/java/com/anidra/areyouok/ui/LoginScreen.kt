package com.anidra.areyouok.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.components.AuthLabel
import com.anidra.areyouok.components.AuthLinkText
import com.anidra.areyouok.components.AuthPrimaryButton
import com.anidra.areyouok.components.GlassCard
import com.anidra.areyouok.components.GlassyTextField
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anidra.areyouok.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onLogin(email.trim(), password)
        }
    }

    AuthBackground(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "Vitalleads",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthColors.Title,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Welcome back",
                fontSize = 18.sp,
                color = AuthColors.Subtitle,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(44.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 380.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    AuthLabel("Email")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Password")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailing = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) {
                                        Icons.Outlined.VisibilityOff
                                    } else {
                                        Icons.Outlined.Visibility
                                    },
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = AuthColors.FieldIcon
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AuthLinkText(
                            text = "Forgot Password?",
                            onClick = onForgotPassword
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    AuthPrimaryButton(
                        text = if (uiState.loading) "Logging in..." else "Log In",
                        icon = Icons.AutoMirrored.Outlined.Login,
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.login(email.trim(), password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    )

                    if (uiState.loading) {
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error?.let { errorMessage ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account? ",
                    color = AuthColors.Subtitle,
                    fontSize = 16.sp
                )
                AuthLinkText(
                    text = "Sign Up",
                    onClick = onSignUp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}