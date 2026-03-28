package com.anidra.areyouok.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
private data class WatchUi(
    val compact: Boolean,
    val horizontalPadding: Dp,
    val topPadding: Dp,
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
    val avatarSize: Dp,
    val addHeroSize: Dp
)

@Composable
private fun rememberWatchUi(): WatchUi {
    val config = LocalConfiguration.current
    val compact = config.screenWidthDp < 390 || config.screenHeightDp < 760

    return if (compact) {
        WatchUi(
            compact = true,
            horizontalPadding = 16.dp,
            topPadding = 34.dp,
            sectionSpacing = 12.dp,
            cardPadding = 14.dp,
            titleSize = 22.sp,
            sectionTitleSize = 18.sp,
            nameSize = 20.sp,
            bodySize = 14.sp,
            captionSize = 12.sp,
            statValueSize = 28.sp,
            buttonHeight = 54.dp,
            mapHeight = 140.dp,
            avatarSize = 52.dp,
            addHeroSize = 72.dp
        )
    } else {
        WatchUi(
            compact = false,
            horizontalPadding = 20.dp,
            topPadding = 34.dp,
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
            avatarSize = 60.dp,
            addHeroSize = 86.dp
        )
    }
}