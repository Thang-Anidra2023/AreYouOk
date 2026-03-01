package com.anidra.areyouok.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.components.AuthLabel
import com.anidra.areyouok.components.AuthLinkText
import com.anidra.areyouok.components.AuthPrimaryButton
import com.anidra.areyouok.components.GlassCard
import com.anidra.areyouok.components.GlassyTextField


@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onCreateAccount: (email: String, password: String, confirmPassword: String) -> Unit = { _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AuthBackground(modifier = modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
            keyboardController?.hide()
        })
    }) {
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
                text = "Create your account",
                fontSize = 18.sp,
                color = AuthColors.Subtitle,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(44.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 430.dp)
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
                        placeholder = "Create a password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailing = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = AuthColors.FieldIcon
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Confirm Password")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm your password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailing = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = AuthColors.FieldIcon
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(22.dp))

                    AuthPrimaryButton(
                        text = "Create Account",
                        icon = Icons.Outlined.PersonAdd,
                        onClick = { onCreateAccount(email.trim(), password, confirmPassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Already have an account? ",
                    color = AuthColors.Subtitle,
                    fontSize = 16.sp
                )
                AuthLinkText(
                    text = "Log In",
                    onClick = onLoginClick,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(
    name = "Register",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            onCreateAccount = { _, _, _ -> },
            onLoginClick = {}
        )
    }
}