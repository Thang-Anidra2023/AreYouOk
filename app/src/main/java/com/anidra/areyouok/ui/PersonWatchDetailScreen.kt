package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors

@Composable
fun PersonWatchDetailScreen(
    personId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenMapsClick: (String) -> Unit = {}
) {
    val ui = rememberWatchUiSpec()
    val person = remember(personId) { PeopleIWatchMockData.byId(personId) }
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    if (person == null) {
        AuthBackground(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Person not found",
                    color = AuthColors.Title
                )
            }
        }
        return
    }

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
                    text = person.name,
                    color = AuthColors.Title,
                    fontSize = ui.titleSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                            RoundedCornerShape(999.dp)
                        )
                        .clickable(onClick = onBack)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFE1896E)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Back",
                        color = Color(0xFFE1896E),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            WatchGlassCard(cardPadding = ui.cardPadding) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WatchAvatarCircle(
                        letter = person.name.first().uppercase(),
                        size = ui.avatarSize
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = person.name,
                            color = AuthColors.Title,
                            fontSize = if (ui.compact) 24.sp else 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = person.email,
                            color = AuthColors.Subtitle,
                            fontSize = ui.bodySize
                        )
                    }
                }
            }

            WatchGlassCard(cardPadding = ui.cardPadding) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WatchStatusCircle(
                        status = person.status,
                        size = ui.largeBadgeSize
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (person.status == WatchStatus.CHECKED_IN) {
                            "Checked In"
                        } else {
                            "Waiting for response"
                        },
                        color = AuthColors.Title,
                        fontSize = if (ui.compact) 26.sp else 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Last check-in: ${person.checkedInAgo ?: "Not available"}",
                        color = AuthColors.Subtitle,
                        fontSize = ui.bodySize
                    )
                }
            }

            WatchGlassCard(cardPadding = ui.cardPadding) {
                Text(
                    text = "Last Known Location",
                    color = AuthColors.Title,
                    fontSize = ui.sectionTitleSize,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ui.mapHeight)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mock map preview",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = ui.bodySize
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.NearMe,
                        contentDescription = null,
                        tint = Color(0xFFE1896E)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = person.location ?: "No location available",
                        color = AuthColors.Title,
                        fontSize = ui.bodySize
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Open in Maps",
                    color = Color(0xFFE1896E),
                    fontSize = ui.bodySize,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        person.location?.let(onOpenMapsClick)
                    }
                )
            }

            WatchGlassCard(cardPadding = ui.cardPadding) {
                Text(
                    text = "Check-in Schedule",
                    color = AuthColors.Title,
                    fontSize = ui.sectionTitleSize,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily at",
                            color = AuthColors.Subtitle,
                            fontSize = ui.captionSize
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = person.dailyCheckInTime,
                            color = AuthColors.Title,
                            fontSize = if (ui.compact) 24.sp else 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Window",
                            color = AuthColors.Subtitle,
                            fontSize = ui.captionSize
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = person.windowHours,
                            color = AuthColors.Title,
                            fontSize = if (ui.compact) 24.sp else 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}