package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.components.GlassCard
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.viewmodel.AccountViewModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class AlertChannelPreference {
    EMAIL, SMS, BOTH
}

data class AccountUiModel(
    val id: String,
    val email: String,
    val createdAt: String,
    val lastLoginDate: String?,
    val inactivityThresholdDays: Int?,
    val alertChannelPreference: AlertChannelPreference,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String?
)

@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    account: AccountUiModel,
    onEditProfile: () -> Unit = {}
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val fullName = remember(account.firstName, account.lastName) {
        listOf(account.firstName, account.lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "Unnamed User" }
    }

    AuthBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding + 50.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AccountTopBar(onEditProfile = onEditProfile)
            }

            item {
                AccountHeroCard(
                    fullName = fullName,
                    email = account.email,
                    alertPreference = account.alertChannelPreference
                )
            }

            item {
                AccountSectionCard(title = "Profile") {
                    AccountInfoRow(
                        icon = Icons.Outlined.Badge,
                        label = "User ID",
                        value = account.id,
                        monospace = true
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.Outlined.PersonOutline,
                        label = "First name",
                        value = account.firstName.ifBlank { "Not set" }
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.Outlined.PersonOutline,
                        label = "Last name",
                        value = account.lastName.ifBlank { "Not set" }
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = account.email
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Mobile number",
                        value = account.mobileNumber?.ifBlank { "Not set" } ?: "Not set"
                    )
                }
            }

            item {
                AccountSectionCard(title = "Activity") {
                    AccountInfoRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Created at",
                        value = formatDateTime(account.createdAt)
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.AutoMirrored.Outlined.Login,
                        label = "Last login",
                        value = formatDateTime(account.lastLoginDate)
                    )
                }
            }

            item {
                AccountSectionCard(title = "Safety preferences") {
                    AccountInfoRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Inactivity threshold",
                        value = account.inactivityThresholdDays?.let { days ->
                            "$days day" + if (days == 1) "" else "s"
                        } ?: "Global default"
                    )
                    AccountDivider()

                    AccountInfoRow(
                        icon = Icons.Outlined.NotificationsActive,
                        label = "Alert channel",
                        value = account.alertChannelPreference.toDisplayText()
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountTopBar(
    onEditProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp)
    ) {
        Text(
            text = "Account",
            modifier = Modifier.align(Alignment.Center),
            color = AuthColors.Title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        TextButton(
            onClick = onEditProfile,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = "Edit",
                color = AuthColors.AccentOrange,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AccountHeroCard(
    fullName: String,
    email: String,
    alertPreference: AlertChannelPreference
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(
                initials = fullName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.take(1).uppercase() }
                    .ifBlank { "U" }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fullName,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    color = Color.White.copy(alpha = 0.70f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                PreferenceChip(text = alertPreference.toDisplayText())
            }
        }
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PreferenceChip(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = AuthColors.AccentOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AccountSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 8.dp),
                color = Color.White.copy(alpha = 0.62f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp
            )

            content()
        }
    }
}

@Composable
private fun AccountInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    monospace: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.88f)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = if (monospace) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default
            )
        }
    }
}

@Composable
private fun AccountDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.08f)
    )
}

private fun AlertChannelPreference.toDisplayText(): String {
    return when (this) {
        AlertChannelPreference.EMAIL -> "Email"
        AlertChannelPreference.SMS -> "SMS"
        AlertChannelPreference.BOTH -> "Email + SMS"
    }
}

private fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "Never"

    return try {
        val instant = try {
            Instant.parse(value)
        } catch (_: Exception) {
            OffsetDateTime.parse(value).toInstant()
        }

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • h:mm a", Locale.getDefault())
        formatter.format(instant.atZone(ZoneId.systemDefault()))
    } catch (_: Exception) {
        value
    }
}