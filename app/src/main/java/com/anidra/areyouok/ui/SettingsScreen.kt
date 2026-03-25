package com.anidra.areyouok.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.anidra.areyouok.permissions.PermissionState
import com.anidra.areyouok.util.findActivity
import com.anidra.areyouok.util.openAppSettings
import com.anidra.areyouok.util.openNotificationSettings
import com.anidra.areyouok.viewmodel.SettingsViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onCheckInTimeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refresh(activity)
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

    DisposableEffect(lifecycleOwner, activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh(activity)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    SettingsScreen(
        notificationsEnabled = uiState.notifications.isGranted,
        locationEnabled = uiState.location.isGranted,
        motionFitnessEnabled = uiState.motion.isGranted,
        onCheckInTimeClick = onCheckInTimeClick,

        onNotificationsClick = {
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
        },

        onLocationClick = {
            when (uiState.location.state) {
                PermissionState.GRANTED -> Unit
                PermissionState.PERMANENTLY_DENIED -> context.openAppSettings()
                PermissionState.DENIED,
                PermissionState.NOT_REQUIRED -> {
                    viewModel.markLocationAsked()
                    locationLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }
                PermissionState.UNSUPPORTED -> Unit
            }
        },

        onMotionFitnessClick = {
            when (uiState.motion.state) {
                PermissionState.GRANTED,
                PermissionState.NOT_REQUIRED -> Unit

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
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    reminderEnabled: Boolean = true,
    autoCheckInEnabled: Boolean = false,
    checkInTimeText: String = "Daily by 10:40",
    notificationsEnabled: Boolean = false,
    locationEnabled: Boolean = true,
    motionFitnessEnabled: Boolean = true,
    versionName: String = "1.1.2",
    onCheckInTimeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onMotionFitnessClick: () -> Unit = {},
) {
    var monitoringActive by remember { mutableStateOf(reminderEnabled) }
    var autoCheckIn by remember { mutableStateOf(autoCheckInEnabled) }

    AuthBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 0.dp)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp)
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
                    title = "Monitoring Active",
                    subtitle = "Get daily check-in reminders",
                    checked = monitoringActive,
                    onCheckedChange = { monitoringActive = it }
                )

                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Outlined.DirectionsWalk,
                    iconBg = Color(0xFFE4F1E7),
                    iconTint = Color(0xFF76B486),
                    title = "Auto Check-in by Movement",
                    subtitle = "Check in automatically when active",
                    checked = autoCheckIn,
                    onCheckedChange = { autoCheckIn = it }
                )

                SettingsDivider()

                SettingsNavigationRow(
                    icon = Icons.Outlined.AccessTime,
                    iconBg = Color(0xFFE5EEF8),
                    iconTint = Color(0xFF8CB2D8),
                    title = "Check-in Time",
                    subtitle = checkInTimeText,
                    onClick = onCheckInTimeClick
                )
            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("PERMISSIONS")
            Spacer(Modifier.height(10.dp))
            SettingsSectionCard {
                SettingsStatusRow(
                    icon = Icons.Outlined.Notifications,
                    iconBg = Color(0xFFFFEDD9),
                    iconTint = Color(0xFFE4A45E),
                    title = "Notifications",
                    subtitle = "Allow reminder alerts",
                    statusText = if (notificationsEnabled) "Enabled" else "Enable",
                    statusColor = if (notificationsEnabled) Color(0xFF8BC48E) else Color(0xFFE1896E),
                    onClick = onNotificationsClick
                )

                SettingsDivider()

                SettingsStatusRow(
                    icon = Icons.Outlined.LocationOn,
                    iconBg = Color(0xFFF0E8FA),
                    iconTint = Color(0xFFB19BD8),
                    title = "Location",
                    subtitle = "Include in alerts (optional)",
                    statusText = if (locationEnabled) "Enabled" else "Enable",
                    statusColor = if (locationEnabled) Color(0xFF8BC48E) else Color(0xFFE1896E),
                    onClick = onLocationClick
                )

                SettingsDivider()

                SettingsStatusRow(
                    icon = Icons.Outlined.DirectionsWalk,
                    iconBg = Color(0xFFE7F2E8),
                    iconTint = Color(0xFF7FB488),
                    title = "Motion & Fitness",
                    subtitle = "Step counting for auto check-in",
                    statusText = if (motionFitnessEnabled) "Enabled" else "Enable",
                    statusColor = if (motionFitnessEnabled) Color(0xFF8BC48E) else Color(0xFFE1896E),
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
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
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

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun SettingsStatusRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    statusText: String,
    statusColor: Color,
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

        Text(
            text = statusText,
            color = statusColor,
            fontSize = 16.sp,
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