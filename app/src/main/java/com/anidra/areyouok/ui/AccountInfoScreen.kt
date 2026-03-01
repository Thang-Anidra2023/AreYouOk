package com.anidra.areyouok.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    name: String = "Alex",
    email: String = "alex@email.com",
    phone: String = "+61 4xx xxx xxx",
    membership: String = "Standard",
    onEditProfile: () -> Unit = {}
) {
    AuthBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                // keep content away from your top-left menu button
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 92.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Account Info",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthColors.Title
            )

            Spacer(Modifier.height(18.dp))

            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleIcon(Icons.Outlined.PersonOutline)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = name,
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Member: $membership",
                            color = Color.White.copy(alpha = 0.70f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    GlassInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = email
                    )
                    GlassInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone",
                        value = phone
                    )
                    GlassInfoRow(
                        icon = Icons.Outlined.Security,
                        label = "Security",
                        value = "Password • Face/Touch ID"
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onEditProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(2.dp, AuthColors.AccentOrange),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.ButtonFill,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    val fill = Color(0xFF242C3E).copy(alpha = 0.62f)
    val border = Color.White.copy(alpha = 0.14f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, shape)
            .clip(shape)
            .border(BorderStroke(1.dp, border), shape),
        color = fill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun CircleIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.92f))
    }
}

@Composable
private fun GlassInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircleIcon(icon)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(text = label, color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp)
            Text(text = value, color = Color.White.copy(alpha = 0.92f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}