package com.anidra.areyouok.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.permissions.PermissionItemUi
import com.anidra.areyouok.permissions.PermissionState
import com.anidra.areyouok.util.findActivity
import com.anidra.areyouok.util.openAppSettings
import com.anidra.areyouok.util.openNotificationSettings
import com.anidra.areyouok.viewmodel.SettingsViewModel
import com.anidra.areyouok.BuildConfig

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()

    var autoCheckInEnabled by rememberSaveable { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refresh(activity)
        viewModel.reconcileReminderSchedule()
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refresh(activity)
    }

    val motionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refresh(activity)
    }

    LaunchedEffect(activity) {
        viewModel.refresh(activity)
        viewModel.reconcileReminderSchedule()
    }

    DisposableEffect(lifecycleOwner, activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh(activity)
                viewModel.reconcileReminderSchedule()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.motion.state) {
        if (!uiState.motion.isGranted) {
            autoCheckInEnabled = false
        }
    }

    fun requestNotificationsOrOpenSettings() {
        when (uiState.notifications.state) {
            PermissionState.GRANTED,
            PermissionState.NOT_REQUIRED -> context.openNotificationSettings()

            PermissionState.PERMANENTLY_DENIED -> context.openAppSettings()

            PermissionState.DENIED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.markNotificationsAsked()
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    context.openNotificationSettings()
                }
            }

            PermissionState.UNSUPPORTED -> Unit
        }
    }

    fun requestLocationOrOpenSettings() {
        when (uiState.location.state) {
            PermissionState.GRANTED,
            PermissionState.NOT_REQUIRED -> context.openAppSettings()

            PermissionState.PERMANENTLY_DENIED -> context.openAppSettings()

            PermissionState.DENIED -> {
                viewModel.markLocationAsked()
                locationLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }

            PermissionState.UNSUPPORTED -> Unit
        }
    }

    fun requestMotionOrOpenSettings() {
        when (uiState.motion.state) {
            PermissionState.GRANTED,
            PermissionState.NOT_REQUIRED -> context.openAppSettings()

            PermissionState.PERMANENTLY_DENIED -> context.openAppSettings()

            PermissionState.DENIED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    viewModel.markMotionAsked()
                    motionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }

            PermissionState.UNSUPPORTED -> Unit
        }
    }

    SettingsScreen(
        reminderEnabled = remindersEnabled,
        onReminderEnabledChange = { checked ->
            if (uiState.notifications.isGranted) {
                viewModel.setCheckInRemindersEnabled(checked)
            } else {
                requestNotificationsOrOpenSettings()
            }
        },
        autoCheckInEnabled = autoCheckInEnabled,
        onAutoCheckInEnabledChange = { checked ->
            if (uiState.motion.isGranted) {
                autoCheckInEnabled = checked
            } else {
                requestMotionOrOpenSettings()
            }
        },
        notifications = uiState.notifications,
        location = uiState.location,
        motion = uiState.motion,
        versionName = BuildConfig.VERSION_NAME,
        onNotificationsClick = ::requestNotificationsOrOpenSettings,
        onLocationClick = ::requestLocationOrOpenSettings,
        onMotionFitnessClick = ::requestMotionOrOpenSettings
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    reminderEnabled: Boolean = true,
    autoCheckInEnabled: Boolean = false,
    notifications: PermissionItemUi = PermissionItemUi(),
    location: PermissionItemUi = PermissionItemUi(),
    motion: PermissionItemUi = PermissionItemUi(),
    versionName: String = "1.1.2",
    onReminderEnabledChange: (Boolean) -> Unit = {},
    onAutoCheckInEnabledChange: (Boolean) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onMotionFitnessClick: () -> Unit = {},
) {
    val grantedCount = listOf(notifications, location, motion).count { it.isGranted }

    AuthBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp)
                .padding(horizontal = 4.dp)
        ) {
            SettingsTopBar()

            Spacer(Modifier.height(50.dp))

            SectionTitle("CHECK-IN SETTINGS")
            Spacer(Modifier.height(10.dp))
            SettingsSectionCard {
                SettingsToggleRow(
                    icon = Icons.Outlined.Notifications,
                    iconBg = Color(0xFFFFE4DC),
                    iconTint = Color(0xFFE1896E),
                    title = "Check-in Reminders",
                    subtitle = if (notifications.isGranted) {
                        "Get daily check-in reminders"
                    } else {
                        "Enable Notifications permission first"
                    },
                    checked = reminderEnabled,
                    enabled = notifications.isGranted,
                    onCheckedChange = onReminderEnabledChange,
                    onRowClick = if (!notifications.isGranted) onNotificationsClick else null
                )

                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Outlined.DirectionsWalk,
                    iconBg = Color(0xFFE4F1E7),
                    iconTint = Color(0xFF76B486),
                    title = "Auto Check-in by Movement",
                    subtitle = if (motion.isGranted) {
                        "Check in automatically when activity is detected"
                    } else {
                        "Enable Motion & Fitness permission first"
                    },
                    checked = autoCheckInEnabled,
                    enabled = motion.isGranted,
                    onCheckedChange = onAutoCheckInEnabledChange,
                    onRowClick = if (!motion.isGranted) onMotionFitnessClick else null
                )

            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("PERMISSIONS")
            Spacer(Modifier.height(10.dp))
            PermissionSummaryCard(
                enabledCount = grantedCount,
                totalCount = 3
            )

            Spacer(Modifier.height(10.dp))

            SettingsSectionCard {
                SettingsStatusRow(
                    icon = Icons.Outlined.Notifications,
                    iconBg = Color(0xFFFFEDD9),
                    iconTint = Color(0xFFE4A45E),
                    title = "Notifications",
                    subtitle = permissionSubtitle(
                        item = notifications,
                        grantedText = "Required for reminder alerts",
                        deniedText = "Tap to allow reminder alerts",
                        settingsText = "Open Settings to enable notifications",
                        unsupportedText = "Notifications are not supported on this device"
                    ),
                    item = notifications,
                    onClick = onNotificationsClick
                )

                SettingsDivider()

                SettingsStatusRow(
                    icon = Icons.Outlined.LocationOn,
                    iconBg = Color(0xFFF0E8FA),
                    iconTint = Color(0xFFB19BD8),
                    title = "Location",
                    subtitle = permissionSubtitle(
                        item = location,
                        grantedText = "Optional, included in emergency alerts",
                        deniedText = "Tap to allow location access",
                        settingsText = "Open Settings to enable location access",
                        unsupportedText = "Location is not supported on this device"
                    ),
                    item = location,
                    onClick = onLocationClick
                )

                SettingsDivider()

                SettingsStatusRow(
                    icon = Icons.Outlined.DirectionsWalk,
                    iconBg = Color(0xFFE7F2E8),
                    iconTint = Color(0xFF7FB488),
                    title = "Motion & Fitness",
                    subtitle = permissionSubtitle(
                        item = motion,
                        grantedText = "Required for automatic check-in",
                        deniedText = "Tap to allow activity recognition",
                        settingsText = "Open Settings to enable activity recognition",
                        unsupportedText = "Motion detection is not supported on this device"
                    ),
                    item = motion,
                    onClick = onMotionFitnessClick
                )
            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("ABOUT")
            Spacer(Modifier.height(10.dp))
            SettingsSectionCard {
                SettingsValueRow(
                    icon = Icons.Outlined.Info,
                    iconBg = Color.White.copy(alpha = 0.10f),
                    iconTint = Color.White.copy(alpha = 0.72f),
                    title = "Version",
                    value = versionName
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

private fun permissionSubtitle(
    item: PermissionItemUi,
    grantedText: String,
    deniedText: String,
    settingsText: String,
    unsupportedText: String
): String {
    return when (item.state) {
        PermissionState.GRANTED,
        PermissionState.NOT_REQUIRED -> grantedText

        PermissionState.DENIED -> deniedText
        PermissionState.PERMANENTLY_DENIED -> settingsText
        PermissionState.UNSUPPORTED -> unsupportedText
    }
}

@Composable
private fun SettingsTopBar() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Settings",
            color = AuthColors.Title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.58f),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun PermissionSummaryCard(
    enabledCount: Int,
    totalCount: Int
) {
    val shape = RoundedCornerShape(20.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = AuthColors.Shadow,
                spotColor = AuthColors.Shadow
            )
            .clip(shape)
            .border(
                border = BorderStroke(1.dp, AuthColors.GlassBorder),
                shape = shape
            ),
        color = AuthColors.GlassFill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabledCount == totalCount) Color(0xFF8BC48E) else Color(0xFFE4A45E)
                    )
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Permission status",
                    color = AuthColors.Title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$enabledCount of $totalCount permissions enabled",
                    color = AuthColors.Subtitle,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = AuthColors.Shadow,
                spotColor = AuthColors.Shadow
            )
            .clip(shape)
            .border(
                border = BorderStroke(1.dp, AuthColors.GlassBorder),
                shape = shape
            ),
        color = AuthColors.GlassFill,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRowClick: (() -> Unit)? = null
) {
    val rowModifier = if (onRowClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconBg, iconTint)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AuthColors.Title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = if (enabled) AuthColors.Subtitle else AuthColors.Subtitle.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String?,
    titleColor: Color = AuthColors.Title,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconBg, iconTint)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = it,
                    color = AuthColors.Subtitle,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsStatusRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    item: PermissionItemUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.state != PermissionState.UNSUPPORTED) { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconBg, iconTint)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AuthColors.Title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = AuthColors.Subtitle,
                fontSize = 14.sp
            )
        }

        PermissionBadge(item = item)
    }
}

@Composable
private fun PermissionBadge(
    item: PermissionItemUi
) {
    val tint = when (item.state) {
        PermissionState.GRANTED,
        PermissionState.NOT_REQUIRED -> Color(0xFF8BC48E)

        PermissionState.DENIED -> Color(0xFFE1896E)
        PermissionState.PERMANENTLY_DENIED -> Color(0xFF8CB2D8)
        PermissionState.UNSUPPORTED -> Color(0xFFA7A7A7)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f))
            .border(
                width = 1.dp,
                color = tint.copy(alpha = 0.45f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.statusText,
            color = tint,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SettingsValueRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconBg, iconTint)

        Spacer(Modifier.width(14.dp))

        Text(
            text = title,
            color = AuthColors.Title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 18.sp
        )
    }
}

@Composable
private fun SettingIcon(
    icon: ImageVector,
    bg: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        color = Color.White.copy(alpha = 0.10f),
        thickness = 1.dp
    )
}