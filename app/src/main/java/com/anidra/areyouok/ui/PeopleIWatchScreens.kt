package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors

enum class WatchStatus {
    CHECKED_IN,
    WAITING_FOR_RESPONSE,
    PENDING_INVITE
}

data class WatchedPersonUi(
    val id: String,
    val name: String,
    val email: String,
    val status: WatchStatus,
    val checkedInAgo: String? = null,
    val location: String? = null,
    val inviteSentAgo: String? = null,
    val dailyCheckInTime: String = "9:00 AM",
    val windowHours: String = "2h"
)

object PeopleIWatchMockData {
    val people = listOf(
        WatchedPersonUi(
            id = "mom",
            name = "Mom",
            email = "mom@areyouok.app",
            status = WatchStatus.WAITING_FOR_RESPONSE
        ),
        WatchedPersonUi(
            id = "dad",
            name = "Dad",
            email = "dad@areyouok.app",
            status = WatchStatus.CHECKED_IN,
            checkedInAgo = "1 minute ago",
            location = "Mesa Geitonia, Limassol, Cyprus"
        ),
        WatchedPersonUi(
            id = "daughter",
            name = "Daughter",
            email = "daughter@areyouok.app",
            status = WatchStatus.CHECKED_IN,
            checkedInAgo = "23 seconds ago",
            location = "Mesa Geitonia, Limassol, Cyprus"
        ),
        WatchedPersonUi(
            id = "son",
            name = "Son",
            email = "son@areyouok.app",
            status = WatchStatus.PENDING_INVITE,
            inviteSentAgo = "8 minutes ago"
        )
    )

    fun activePeople(): List<WatchedPersonUi> =
        people.filter { it.status != WatchStatus.PENDING_INVITE }

    fun pendingPeople(): List<WatchedPersonUi> =
        people.filter { it.status == WatchStatus.PENDING_INVITE }

    fun byId(id: String): WatchedPersonUi? =
        people.firstOrNull { it.id == id }
}

@Immutable
internal data class WatchUiSpec(
    val compact: Boolean,
    val horizontalPadding: Dp,
    val sectionSpacing: Dp,
    val cardPadding: Dp,
    val titleSize: TextUnit,
    val sectionTitleSize: TextUnit,
    val nameSize: TextUnit,
    val bodySize: TextUnit,
    val captionSize: TextUnit,
    val statValueSize: TextUnit,
    val buttonHeight: Dp,
    val mapHeight: Dp,
    val heroSize: Dp,
    val avatarSize: Dp,
    val smallBadgeSize: Dp,
    val largeBadgeSize: Dp
)

@Composable
internal fun rememberWatchUiSpec(): WatchUiSpec {
    val config = LocalConfiguration.current
    val compact = config.screenWidthDp <= 360 || config.screenHeightDp <= 720

    return if (compact) {
        WatchUiSpec(
            compact = true,
            horizontalPadding = 0.dp,
            sectionSpacing = 12.dp,
            cardPadding = 14.dp,
            titleSize = 22.sp,
            sectionTitleSize = 18.sp,
            nameSize = 20.sp,
            bodySize = 14.sp,
            captionSize = 12.sp,
            statValueSize = 28.sp,
            buttonHeight = 54.dp,
            mapHeight = 150.dp,
            heroSize = 72.dp,
            avatarSize = 52.dp,
            smallBadgeSize = 40.dp,
            largeBadgeSize = 78.dp
        )
    } else {
        WatchUiSpec(
            compact = false,
            horizontalPadding = 0.dp,
            sectionSpacing = 16.dp,
            cardPadding = 18.dp,
            titleSize = 24.sp,
            sectionTitleSize = 20.sp,
            nameSize = 24.sp,
            bodySize = 16.sp,
            captionSize = 14.sp,
            statValueSize = 34.sp,
            buttonHeight = 62.dp,
            mapHeight = 180.dp,
            heroSize = 86.dp,
            avatarSize = 60.dp,
            smallBadgeSize = 44.dp,
            largeBadgeSize = 88.dp
        )
    }
}

@Composable
fun PeopleIWatchScreen(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onPersonClick: (String) -> Unit = {},
    onResendInviteClick: (String) -> Unit = {}
) {
    val ui = rememberWatchUiSpec()
    val active = remember { PeopleIWatchMockData.activePeople() }
    val pending = remember { PeopleIWatchMockData.pendingPeople() }
    val checkedInCount = active.count { it.status == WatchStatus.CHECKED_IN }
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    AuthBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = ui.horizontalPadding,
                end = ui.horizontalPadding,
                top = topInset + 14.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(ui.sectionSpacing)
        ) {
            item {
                WatchTitleBar(
                    title = "People I Watch",
                    titleSize = ui.titleSize,
                    onAddClick = onAddClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(if (ui.compact) 14.dp else 20.dp))
            }

            item {
                WatchGlassCard(cardPadding = ui.cardPadding) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WatchStatBlock(
                            value = checkedInCount.toString(),
                            label = "Checked In",
                            valueColor = Color(0xFF8BC48E),
                            valueSize = ui.statValueSize,
                            labelSize = ui.bodySize
                        )
                        WatchStatBlock(
                            value = active.size.toString(),
                            label = "Total",
                            valueColor = Color(0xFFE1896E),
                            valueSize = ui.statValueSize,
                            labelSize = ui.bodySize
                        )
                    }
                }
            }

            items(active, key = { it.id }) { person ->
                ActiveWatchPersonCard(
                    person = person,
                    ui = ui,
                    onClick = { onPersonClick(person.id) }
                )
            }

            if (pending.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Invites",
                        color = AuthColors.Title,
                        fontSize = ui.sectionTitleSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(pending, key = { it.id }) { person ->
                    PendingInviteCard(
                        person = person,
                        ui = ui,
                        onResend = { onResendInviteClick(person.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun WatchTitleBar(
    title: String,
    titleSize: TextUnit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(72.dp))

        Text(
            text = title,
            color = AuthColors.Title,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.width(72.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8FC2C0).copy(alpha = 0.18f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.18f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF8FC2C0),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
internal fun WatchBackTitleBar(
    leftText: String,
    title: String,
    titleSize: TextUnit,
    onLeftClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftText,
            color = Color(0xFF8FC2C0),
            fontSize = 18.sp,
            modifier = Modifier
                .widthIn(min = 72.dp)
                .clickable { onLeftClick() }
        )

        Text(
            text = title,
            color = AuthColors.Title,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(72.dp))
    }
}

@Composable
internal fun WatchGlassCard(
    modifier: Modifier = Modifier,
    cardPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(18.dp, shape)
            .clip(shape)
            .border(
                BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shape
            ),
        color = Color(0xFF242C3E).copy(alpha = 0.62f),
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            content = content
        )
    }
}

@Composable
internal fun WatchStatBlock(
    value: String,
    label: String,
    valueColor: Color,
    valueSize: TextUnit,
    labelSize: TextUnit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = valueColor,
            fontSize = valueSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = AuthColors.Subtitle,
            fontSize = labelSize
        )
    }
}

@Composable
internal fun ActiveWatchPersonCard(
    person: WatchedPersonUi,
    ui: WatchUiSpec,
    onClick: () -> Unit
) {
    WatchGlassCard(
        modifier = Modifier.clickable { onClick() },
        cardPadding = ui.cardPadding
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WatchStatusCircle(
                status = person.status,
                size = ui.smallBadgeSize
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    color = AuthColors.Title,
                    fontSize = ui.nameSize,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (person.status) {
                        WatchStatus.CHECKED_IN -> "Checked in ${person.checkedInAgo.orEmpty()}"
                        WatchStatus.WAITING_FOR_RESPONSE -> "Waiting for response"
                        WatchStatus.PENDING_INVITE -> "Pending"
                    },
                    color = AuthColors.Subtitle,
                    fontSize = ui.bodySize
                )

                if (!person.location.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.60f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = person.location,
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = ui.captionSize
                        )
                    }
                }
            }

            Text(
                text = "›",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = if (ui.compact) 24.sp else 28.sp
            )
        }
    }
}

@Composable
internal fun PendingInviteCard(
    person: WatchedPersonUi,
    ui: WatchUiSpec,
    onResend: () -> Unit
) {
    WatchGlassCard(cardPadding = ui.cardPadding) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            WatchStatusCircle(
                status = WatchStatus.PENDING_INVITE,
                size = ui.smallBadgeSize
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    color = AuthColors.Title,
                    fontSize = ui.nameSize,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = person.email,
                    color = AuthColors.Subtitle,
                    fontSize = ui.bodySize
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sent ${person.inviteSentAgo.orEmpty()} ago",
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = ui.captionSize
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Resend Invite Email",
                    color = Color(0xFFE96D58),
                    fontSize = ui.bodySize,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onResend() }
                )
            }

            Text(
                text = "Pending",
                color = Color(0xFFE4A45E),
                fontSize = ui.bodySize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun WatchStatusCircle(
    status: WatchStatus,
    size: Dp
) {
    val bg = when (status) {
        WatchStatus.CHECKED_IN -> Color(0xFFE4F1E7)
        WatchStatus.WAITING_FOR_RESPONSE -> Color(0xFFFFEBD5)
        WatchStatus.PENDING_INVITE -> Color(0xFFFFE4DC)
    }

    val tint = when (status) {
        WatchStatus.CHECKED_IN -> Color(0xFF76B486)
        WatchStatus.WAITING_FOR_RESPONSE -> Color(0xFFE4A45E)
        WatchStatus.PENDING_INVITE -> Color(0xFFE1896E)
    }

    val icon = when (status) {
        WatchStatus.CHECKED_IN -> Icons.Outlined.CheckCircle
        WatchStatus.WAITING_FOR_RESPONSE -> Icons.Outlined.WarningAmber
        WatchStatus.PENDING_INVITE -> Icons.Outlined.MailOutline
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
internal fun WatchAvatarCircle(
    letter: String,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = Color(0xFF8BC48E),
            fontSize = if (size <= 52.dp) 24.sp else 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun WatchTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    compact: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            color = AuthColors.Title,
            fontSize = if (compact) 14.sp else 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType
            ),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                focusedBorderColor = Color.White.copy(alpha = 0.22f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = if (compact) 16.sp else 18.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}