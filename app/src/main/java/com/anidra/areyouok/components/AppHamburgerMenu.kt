package com.anidra.areyouok.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.automirrored.outlined.ExitToApp

@Composable
fun AppHamburgerMenu(
    modifier: Modifier = Modifier,
    onCheckIn: () -> Unit,
    onAccountInfo: () -> Unit,
    onSettings: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    var open by remember { mutableStateOf(false) }

    // Status bar safe padding for edge-to-edge
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // --- Button (top-left) ---
        Box(
            modifier = Modifier
                .padding(start = 18.dp, top = topInset + 18.dp)
                .size(64.dp)
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.10f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(20.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { open = !open },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = Color.White.copy(alpha = 0.95f)
            )
        }

        // --- Scrim to dismiss when open ---
        AnimatedVisibility(
            visible = open,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { open = false }
            )
        }

        // --- Menu card (anchored below button) ---
        AnimatedVisibility(
            visible = open,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            GlassMenuCard(
                modifier = Modifier
                    .padding(start = 18.dp, top = topInset + 18.dp + 78.dp) // 64 button + gap
            ) {
                GlassMenuItem(
                    icon = Icons.Outlined.CheckCircleOutline,
                    title = "Check In",
                    onClick = {
                        open = false
                        onCheckIn()
                    }
                )

                GlassMenuItem(
                    icon = Icons.Outlined.PersonOutline,
                    title = "Account Info",
                    onClick = {
                        open = false
                        onAccountInfo()
                    }
                )
                GlassMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "Settings",
                    onClick = {
                        open = false
                        onSettings()
                    }
                )
                GlassMenuItem(
                    icon = Icons.Outlined.NotificationsNone,
                    title = "Notifications",
                    onClick = {
                        open = false
                        onNotifications()
                    }
                )
                GlassMenuItem(
                    icon = Icons.AutoMirrored.Outlined.ExitToApp,
                    title = "Log Out",
                    onClick = {
                        open = false
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun GlassMenuCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(34.dp)
    val fill = Color(0xFF242C3E).copy(alpha = 0.78f)
    val border = Color.White.copy(alpha = 0.12f)

    Surface(
        modifier = modifier
            .widthIn(min = 280.dp, max = 360.dp)
            .shadow(
                elevation = 26.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.40f),
                spotColor = Color.Black.copy(alpha = 0.40f)
            )
            .clip(shape)
            .border(BorderStroke(1.dp, border), shape),
        color = fill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            content = content
        )
    }
}

@Composable
private fun GlassMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.92f)
        )
    }
}