package com.anidra.areyouok.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Public
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.components.AuthLabel
import com.anidra.areyouok.components.AuthLinkText
import com.anidra.areyouok.components.AuthPrimaryButton
import com.anidra.areyouok.components.GlassCard
import com.anidra.areyouok.components.GlassyTextField
import com.anidra.areyouok.viewmodel.RegisterForm
import com.anidra.areyouok.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var registrationType by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onRegisterSuccess()
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
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

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

            Spacer(Modifier.height(32.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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

                    AuthLabel("Registration Type")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = registrationType,
                        onValueChange = { registrationType = it },
                        placeholder = "e.g. Standard",
                        leadingIcon = Icons.Outlined.Badge
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("First Name")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Enter your first name",
                        leadingIcon = Icons.Outlined.Person
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Middle Name")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = middleName,
                        onValueChange = { middleName = it },
                        placeholder = "Enter your middle name",
                        leadingIcon = Icons.Outlined.Person
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Last Name")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Enter your last name",
                        leadingIcon = Icons.Outlined.Person
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Country")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = country,
                        onValueChange = { country = it },
                        placeholder = "Enter your country",
                        leadingIcon = Icons.Outlined.Public
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("State")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = state,
                        onValueChange = { state = it },
                        placeholder = "Enter your state",
                        leadingIcon = Icons.Outlined.Place
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Mobile Number")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        placeholder = "Enter your mobile number",
                        leadingIcon = Icons.Outlined.Call,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Address Line 1")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = addressLine1,
                        onValueChange = { addressLine1 = it },
                        placeholder = "Enter address line 1",
                        leadingIcon = Icons.Outlined.Home
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Address Line 2")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = addressLine2,
                        onValueChange = { addressLine2 = it },
                        placeholder = "Enter address line 2",
                        leadingIcon = Icons.Outlined.Home
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthLabel("Password")
                    Spacer(Modifier.height(10.dp))
                    GlassyTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Create a password",
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
                        visualTransformation = if (showConfirmPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailing = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) {
                                        Icons.Outlined.VisibilityOff
                                    } else {
                                        Icons.Outlined.Visibility
                                    },
                                    contentDescription = null,
                                    tint = AuthColors.FieldIcon
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(22.dp))

                    AuthPrimaryButton(
                        text = if (uiState.loading) "Creating account..." else "Create Account",
                        icon = Icons.Outlined.PersonAdd,
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()

                            viewModel.register(
                                RegisterForm(
                                    email = email.trim(),
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    registrationType = registrationType,
                                    firstName = firstName,
                                    middleName = middleName,
                                    lastName = lastName,
                                    country = country,
                                    state = state,
                                    mobileNumber = mobileNumber,
                                    addressLine1 = addressLine1,
                                    addressLine2 = addressLine2
                                )
                            )
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

            Spacer(Modifier.height(32.dp))
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
            onRegisterSuccess = {},
            onLoginClick = {}
        )
    }
}