package com.anidra.areyouok.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors

@Composable
fun AddPersonToWatchScreen(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onSendInvite: (name: String, email: String, time: String) -> Unit = { _, _, _ -> }
) {
    val ui = rememberWatchUiSpec()
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var name by remember { mutableStateOf("Mom") }
    var email by remember { mutableStateOf("mom@areyouok.app") }
    var time by remember { mutableStateOf("12:00 PM") }

    AuthBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = ui.horizontalPadding)
                .padding(top = topInset + 56.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(ui.sectionSpacing)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Watch Someone",
                    color = AuthColors.Title,
                    fontSize = ui.titleSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFFE1896E),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            WatchGlassCard(cardPadding = ui.cardPadding) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(ui.heroSize)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4DC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = Color(0xFFE1896E),
                            modifier = Modifier.size(if (ui.compact) 28.dp else 34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(if (ui.compact) 14.dp else 18.dp))

                    Text(
                        text = "Add someone to watch",
                        color = AuthColors.Title,
                        fontSize = if (ui.compact) 24.sp else 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We'll send them an email invite. Once they sign in, everything will be set up automatically.",
                        color = AuthColors.Subtitle,
                        fontSize = ui.bodySize,
                        textAlign = TextAlign.Center
                    )
                }
            }

            WatchTextField(
                label = "Their Name",
                value = name,
                onValueChange = { name = it },
                compact = ui.compact
            )

            WatchTextField(
                label = "Their Email",
                value = email,
                onValueChange = { email = it },
                compact = ui.compact,
                keyboardType = KeyboardType.Email
            )

            Text(
                text = "Check-in Settings",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = ui.captionSize,
                fontWeight = FontWeight.Medium
            )

            WatchTextField(
                label = "Daily Check-in Time",
                value = time,
                onValueChange = { time = it },
                compact = ui.compact,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFE1896E)
                    )
                }
            )

            Text(
                text = "They can change this later",
                color = AuthColors.Subtitle,
                fontSize = ui.captionSize
            )

            Button(
                onClick = { onSendInvite(name, email, time) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ui.buttonHeight),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE96D58),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Send Invite",
                    fontSize = if (ui.compact) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}