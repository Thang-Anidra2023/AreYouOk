package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSendResetLink: (email: String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AuthBackground(modifier = modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
            keyboardController?.hide()
        })
    }) {

        // Top-left back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            BackPillButton(onClick = onBack)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 33.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthColors.Title,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Enter your email and we'll send you instructions to reset\nyour password",
                fontSize = 18.sp,
                color = AuthColors.Subtitle,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(42.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp)
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

                    Spacer(Modifier.height(22.dp))

                    AuthPrimaryButton(
                        text = "Send Reset Link",
                        icon = Icons.Outlined.Send,
                        onClick = { onSendResetLink(email.trim()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Remember your password? ",
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

@Composable
private fun BackPillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // pill matches the glass style but slightly more opaque
    val pillFill = AuthColors.GlassFill.copy(alpha = 0.55f)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .widthIn(min = 140.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = pillFill,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, AuthColors.GlassBorder),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Back",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(
    name = "Forgot Password",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen(
            onBack = {},
            onSendResetLink = {},
            onLoginClick = {}
        )
    }
}