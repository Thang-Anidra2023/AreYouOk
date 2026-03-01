package com.anidra.areyouok.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AuthColors {
    // Background gradient
    val BgTop = Color(0xFF0A1A3A)
    val BgMid = Color(0xFF0B1E45)
    val BgBottom = Color(0xFF07132E)

    // Glass card
    val GlassFill = Color(0xFF22335B).copy(alpha = 0.45f)
    val GlassBorder = Color.White.copy(alpha = 0.14f)

    // Text + field
    val Title = Color.White
    val Subtitle = Color.White.copy(alpha = 0.72f)
    val Label = Color.White.copy(alpha = 0.80f)

    val FieldContainer = Color.White.copy(alpha = 0.12f)
    val FieldText = Color.White.copy(alpha = 0.92f)
    val FieldHint = Color.White.copy(alpha = 0.55f)
    val FieldIcon = Color.White.copy(alpha = 0.65f)

    // Accent + button
    val AccentOrange = Color(0xFFFFA726)
    val ButtonFill = Color(0xFF7A5A55).copy(alpha = 0.85f)

    val Shadow = Color.Black.copy(alpha = 0.30f)
}

object AuthBrushes {
    val Background = Brush.verticalGradient(
        colors = listOf(
            AuthColors.BgTop,
            AuthColors.BgMid,
            AuthColors.BgBottom
        )
    )
}