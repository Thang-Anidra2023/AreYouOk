package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import com.anidra.areyouok.data.room.entity.EmergencyContactSyncState
import com.anidra.areyouok.dialog.EmergencyContactDialog
import com.anidra.areyouok.viewmodel.CheckInViewModel

@Composable
fun CheckInScreen(
    modifier: Modifier = Modifier,
    name: String = "",
    viewModel: CheckInViewModel = hiltViewModel(),
    onCheckIn: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var snackMsg by remember { mutableStateOf<String?>(null) }
    var editingContact by remember { mutableStateOf<EmergencyContactEntity?>(null) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(46.dp)) }

            item {
                Text("Hello,", fontSize = 44.sp, fontWeight = FontWeight.Light, color = AuthColors.Title.copy(0.92f))
                Text(name, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = AuthColors.Title)
            }

            item { Spacer(Modifier.height(44.dp)) }

            item {
                CheckInCircle(
                    checkedIn = state.checkedInToday,
                    onClick = {
                        if (!state.checkedInToday) {
                            viewModel.checkInToday { msg -> snackMsg = msg }
                        }
                    }
                )
            }

            if (state.checkedInToday) {
                val msg = when (state.syncState) {
                    com.anidra.areyouok.data.room.entity.CheckInSyncState.SYNCED ->
                        "✓ Checked In (Synced)"
                    com.anidra.areyouok.data.room.entity.CheckInSyncState.PENDING ->
                        "✓ Checked In (Uploading...)"
                    com.anidra.areyouok.data.room.entity.CheckInSyncState.FAILED ->
                        "✓ Checked In (Upload failed — will auto retry)"
                }
                item { Text(msg, color = SuccessGreen, fontSize = 22.sp, fontWeight = FontWeight.SemiBold) }

                if (state.syncState == com.anidra.areyouok.data.room.entity.CheckInSyncState.FAILED) {
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        Button(onClick = { viewModel.retrySync() }) {
                            Text("Retry Sync")
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }

            // ✅ Contacts list (UPDATED)
            item {
                EmergencyContactsSectionEntity(
                    contacts = state.contacts,
                    onEdit = { editingContact = it; showDialog = true },
                    onDelete = { viewModel.deleteContact(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(Modifier.height(18.dp)) }

            if (state.canAddMore) {
                item {
                    GlassPillButton(
                        text = "Add Emergency Contact",
                        icon = Icons.Outlined.PersonAddAlt1,
                        onClick = { editingContact = null; showDialog = true },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                }
            }

            snackMsg?.let { msg ->
                item { Spacer(Modifier.height(14.dp)) }
                item { Text(msg, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp) }
            }
        }

        if (showDialog) {
            val initial = editingContact

            // NOTE:
            // Your dialog currently returns (name, email, phone).
            // We map:
            // - name  -> label
            // - phone -> mobileNumber
            EmergencyContactDialog(
                initial = initial, // make sure your dialog reads label/email/mobileNumber now
                onDismiss = {
                    showDialog = false
                    editingContact = null
                },
                onConfirm = { nameOrLabel, email, phoneOrMobile ->
                    showDialog = false
                    val editing = editingContact
                    editingContact = null

                    if (editing == null) {
                        viewModel.addContact(
                            label = nameOrLabel,
                            email = email,
                            mobileNumber = phoneOrMobile
                        ) { msg -> snackMsg = msg }
                    } else {
                        viewModel.updateContact(
                            localId = editing.localId,
                            label = nameOrLabel,
                            email = email,
                            mobileNumber = phoneOrMobile
                        ) { msg -> snackMsg = msg }
                    }
                }
            )
        }
    }
}

/** ---------- UI list that uses NEW EmergencyContactEntity ---------- */

@Composable
private fun EmergencyContactsSectionEntity(
    contacts: List<EmergencyContactEntity>,
    onEdit: (EmergencyContactEntity) -> Unit,
    onDelete: (EmergencyContactEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Emergency Contacts",
            color = Color.White.copy(alpha = 0.90f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            contacts.forEach { c ->
                val sync = EmergencyContactSyncState.fromInt(c.syncState)
                val title = c.label?.takeIf { it.isNotBlank() } ?: c.email
                val subtitle = c.mobileNumber

                EmergencyContactRow(
                    title = title,
                    subtitle = subtitle,
                    statusText = when (sync) {
                        EmergencyContactSyncState.SYNCED -> "Synced"
                        EmergencyContactSyncState.PENDING -> "Uploading..."
                        EmergencyContactSyncState.FAILED -> "Failed"
                    },
                    onEdit = { onEdit(c) },
                    onDelete = { onDelete(c) }
                )
            }
        }
    }
}

/** Colors for the “checked in” state */
private val SuccessGreen = Color(0xFF20D6A5)
private val SuccessGlow = SuccessGreen.copy(alpha = 0.28f)

@Composable
private fun CheckInCircle(
    checkedIn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val borderColor = if (checkedIn) SuccessGreen else AuthColors.AccentOrange
    val glowColor = if (checkedIn) SuccessGlow else AuthColors.AccentOrange.copy(alpha = 0.28f)

    val fillBrush = if (checkedIn) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF2AB8A0),
                Color(0xFF1F8F83),
                Color(0xFF16736B)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                AuthColors.ButtonFill.copy(alpha = 0.95f),
                AuthColors.ButtonFill.copy(alpha = 0.80f),
                AuthColors.ButtonFill.copy(alpha = 0.70f),
            )
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 30.dp,
                shape = CircleShape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(CircleShape)
            .background(fillBrush)
            .border(BorderStroke(2.dp, borderColor), CircleShape)
            .clickable(
                enabled = !checkedIn,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checkedIn) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Checked In",
                tint = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.size(96.dp)
            )
        } else {
            Text(
                text = "Check In",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmergencyContactRow(
    title: String,
    subtitle: String,
    statusText: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    val cardFill = AuthColors.GlassFill.copy(alpha = 0.45f)
    val cardBorder = AuthColors.GlassBorder

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        color = cardFill,
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleChip(
                size = 52.dp,
                background = Color.White.copy(alpha = 0.14f),
                border = Color.White.copy(alpha = 0.18f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.95f)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = statusText,
                    color = Color.White.copy(alpha = 0.70f),
                    fontSize = 12.sp
                )
            }

            CircleActionButton(
                background = Color.White.copy(alpha = 0.14f),
                border = Color.White.copy(alpha = 0.18f),
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            CircleActionButton(
                background = Color(0xFFB84B5A).copy(alpha = 0.85f),
                border = Color(0xFFFF8893).copy(alpha = 0.55f),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun CircleChip(
    size: Dp,
    background: Color,
    border: Color,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(BorderStroke(1.dp, border), CircleShape),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun CircleActionButton(
    background: Color,
    border: Color,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(background)
            .border(BorderStroke(1.dp, border), CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun GlassPillButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillFill = AuthColors.GlassFill.copy(alpha = 0.55f)

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = pillFill,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, AuthColors.GlassBorder),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Color.White.copy(alpha = 0.92f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}