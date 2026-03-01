package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val important: Boolean = false
)

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    notifications: List<AppNotification> = sampleNotifications()
) {
    AuthBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 92.dp)
        ) {
            Text(
                text = "Notifications",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthColors.Title
            )

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(notifications, key = { it.id }) { n ->
                    GlassNotificationCard(n)
                }
            }
        }
    }
}

@Composable
private fun GlassNotificationCard(n: AppNotification) {
    val shape = RoundedCornerShape(28.dp)
    val fill = Color(0xFF242C3E).copy(alpha = 0.62f)
    val border = if (n.important) Color(0xFFFF8893).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.14f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, shape)
            .clip(shape)
            .border(BorderStroke(1.dp, border), shape),
        color = fill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp)
        ) {
            Icon(
                imageVector = if (n.important) Icons.Outlined.WarningAmber else Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.90f)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = n.title,
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = n.time,
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = n.message,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun sampleNotifications() = listOf(
    AppNotification("1", "Check-in complete", "Nice work — you checked in successfully.", "Today 9:03am"),
    AppNotification("2", "Reminder", "Don’t forget to check in this afternoon.", "Today 2:00pm"),
    AppNotification("3", "Missed check-in", "You missed a check-in window. Please confirm you’re OK.", "Yesterday 6:10pm", important = true)
)