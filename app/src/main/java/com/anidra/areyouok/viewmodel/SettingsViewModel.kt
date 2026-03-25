package com.anidra.areyouok.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.anidra.areyouok.permissions.PermissionItemUi
import com.anidra.areyouok.permissions.PermissionPrefs
import com.anidra.areyouok.permissions.PermissionStatusChecker
import com.anidra.areyouok.permissions.SettingsPermissionsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    context: Context
) : ViewModel() {

    private val checker = PermissionStatusChecker(context.applicationContext)
    private val prefs = PermissionPrefs(context.applicationContext)

    private val _uiState = MutableStateFlow(SettingsPermissionsUiState())
    val uiState: StateFlow<SettingsPermissionsUiState> = _uiState.asStateFlow()

    fun refresh(activity: Activity?) {
        _uiState.value = SettingsPermissionsUiState(
            notifications = PermissionItemUi(
                checker.notificationState(activity, prefs.wasAsked(PermissionPrefs.NOTIFICATIONS))
            ),
            location = PermissionItemUi(
                checker.locationState(activity, prefs.wasAsked(PermissionPrefs.LOCATION))
            ),
            motion = PermissionItemUi(
                checker.motionState(activity, prefs.wasAsked(PermissionPrefs.MOTION))
            )
        )
    }

    fun markNotificationsAsked() = prefs.markAsked(PermissionPrefs.NOTIFICATIONS)
    fun markLocationAsked() = prefs.markAsked(PermissionPrefs.LOCATION)
    fun markMotionAsked() = prefs.markAsked(PermissionPrefs.MOTION)
}