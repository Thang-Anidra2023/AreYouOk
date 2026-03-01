package com.anidra.areyouok.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuthBrushes.Background)
            .padding(horizontal = 24.dp),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = AuthColors.Shadow,
                spotColor = AuthColors.Shadow
            )
            .clip(RoundedCornerShape(28.dp))
            .border(
                BorderStroke(1.dp, AuthColors.GlassBorder),
                RoundedCornerShape(28.dp)
            ),
        color = AuthColors.GlassFill,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun AuthLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = AuthColors.Label,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

@Composable
fun AuthLinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Text(
        text = text,
        color = AuthColors.AccentOrange,
        fontSize = 16.sp,
        fontWeight = fontWeight,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )
}

@Composable
fun GlassyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailing: (@Composable (() -> Unit))? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        placeholder = { Text(placeholder, color = AuthColors.FieldHint) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = AuthColors.FieldIcon
            )
        },
        trailingIcon = trailing,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AuthColors.FieldContainer,
            unfocusedContainerColor = AuthColors.FieldContainer,
            disabledContainerColor = AuthColors.FieldContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = AuthColors.FieldText,
            unfocusedTextColor = AuthColors.FieldText,
            focusedPlaceholderColor = AuthColors.FieldHint,
            unfocusedPlaceholderColor = AuthColors.FieldHint
        )
    )
}

@Composable
fun AuthPrimaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(2.dp, AuthColors.AccentOrange),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthColors.ButtonFill,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Color.White.copy(alpha = 0.92f)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}