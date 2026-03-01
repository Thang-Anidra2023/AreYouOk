package com.anidra.areyouok.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var dailyReminder by remember { mutableStateOf(true) }
    var notifyEmergencyOnMissedCheckIn by remember { mutableStateOf(false) }
    var soundAlerts by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }

    AuthBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 92.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthColors.Title
            )

            Spacer(Modifier.height(18.dp))

            GlassSettingsCard {
                SettingsToggleRow(
                    icon = Icons.Outlined.NotificationsNone,
                    title = "Daily Check-In Reminder",
                    subtitle = "Send a reminder notification",
                    checked = dailyReminder,
                    onCheckedChange = { dailyReminder = it }
                )

                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Outlined.WarningAmber,
                    title = "Escalate if Missed",
                    subtitle = "Notify emergency contacts if you miss check-in",
                    checked = notifyEmergencyOnMissedCheckIn,
                    onCheckedChange = { notifyEmergencyOnMissedCheckIn = it }
                )

                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Outlined.VolumeUp,
                    title = "Sound Alerts",
                    subtitle = "Play sound for important alerts",
                    checked = soundAlerts,
                    onCheckedChange = { soundAlerts = it }
                )

                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Outlined.Vibration,
                    title = "Vibration",
                    subtitle = "Vibrate for alerts",
                    checked = vibration,
                    onCheckedChange = { vibration = it }
                )
            }

            Spacer(Modifier.height(18.dp))

            GlassSettingsCard {
                SettingsNavRow(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy",
                    subtitle = "Permissions & data usage",
                    onClick = { /* TODO */ }
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help",
                    subtitle = "FAQs and support",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
private fun GlassSettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    val fill = Color(0xFF242C3E).copy(alpha = 0.62f)
    val border = Color.White.copy(alpha = 0.14f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, shape)
            .clip(shape)
            .border(BorderStroke(1.dp, border), shape),
        color = fill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(vertical = 10.dp), content = content)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.90f))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = Color.White.copy(alpha = 0.92f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.90f))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = Color.White.copy(alpha = 0.92f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp)
            }
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.70f)
        )
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        color = Color.White.copy(alpha = 0.10f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}