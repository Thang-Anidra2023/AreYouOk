package com.anidra.areyouok.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity

@Composable
fun EmergencyContactDialog(
    modifier: Modifier = Modifier,
    initial: EmergencyContactEntity? = null,   // null = add, non-null = edit
    onDismiss: () -> Unit,
    onConfirm: (label: String, email: String, mobileNumber: String) -> Unit
) {
    val isEdit = initial != null
    val key = initial?.localId ?: "new"

    // Map: label/name -> label, phone -> mobileNumber
    var label by rememberSaveable(key) { mutableStateOf(initial?.label.orEmpty()) }
    var email by rememberSaveable(key) { mutableStateOf(initial?.email.orEmpty()) }
    var mobile by rememberSaveable(key) { mutableStateOf(initial?.mobileNumber.orEmpty()) }

    // Server requires BOTH email + mobileNumber, label optional
    val canSave = email.trim().isNotBlank() && mobile.trim().isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            GlassDialogCard(modifier = modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEdit) "Edit Emergency Contact" else "Add Emergency Contact",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        CloseCircleButton(onClick = onDismiss)
                    }

                    Spacer(Modifier.height(18.dp))

                    DialogField(
                        value = label,
                        onValueChange = { label = it },
                        placeholder = "Label (optional) — e.g. Mom / Alex",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Spacer(Modifier.height(16.dp))

                    DialogField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email Address (required)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = email.isNotBlank() && !email.contains("@")
                    )

                    Spacer(Modifier.height(16.dp))

                    DialogField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        placeholder = "Phone Number (required)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(Modifier.height(22.dp))

                    SaveContactButton(
                        isEdit = isEdit,
                        enabled = canSave,
                        onClick = {
                            onConfirm(
                                label.trim(),
                                email.trim(),
                                mobile.trim()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassDialogCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val fill = Color(0xFF242C3E).copy(alpha = 0.72f)
    val border = Color.White.copy(alpha = 0.14f)

    Surface(
        modifier = modifier
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(28.dp))
            .border(BorderStroke(1.dp, border), RoundedCornerShape(28.dp)),
        color = fill,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun CloseCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fill = Color.White.copy(alpha = 0.10f)
    val border = Color.White.copy(alpha = 0.20f)

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(fill)
            .border(BorderStroke(1.dp, border), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Close",
            tint = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun DialogField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    isError: Boolean = false
) {
    val shape = RoundedCornerShape(22.dp)
    val container = Color.White.copy(alpha = 0.10f)
    val hint = Color.White.copy(alpha = 0.55f)
    val text = Color.White.copy(alpha = 0.92f)

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        singleLine = true,
        shape = shape,
        placeholder = { Text(placeholder, color = hint) },
        keyboardOptions = keyboardOptions,
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = container,
            unfocusedContainerColor = container,
            disabledContainerColor = container,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = text,
            unfocusedTextColor = text,
            focusedPlaceholderColor = hint,
            unfocusedPlaceholderColor = hint,
            errorContainerColor = container,
            errorTextColor = text,
            errorCursorColor = Color.White,
            errorIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SaveContactButton(
    isEdit: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orange = AuthColors.AccentOrange
    val fill = AuthColors.ButtonFill

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(2.dp, orange),
        colors = ButtonDefaults.buttonColors(
            containerColor = fill,
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = if (isEdit) Icons.Outlined.Edit else Icons.Outlined.PersonAddAlt1,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.92f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (isEdit) "Update Contact" else "Save Contact",
            style = MaterialTheme.typography.titleMedium
        )
    }
}