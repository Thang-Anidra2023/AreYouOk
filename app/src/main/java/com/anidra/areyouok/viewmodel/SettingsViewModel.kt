package com.anidra.areyouok.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.work.CheckInReminderManager
import com.anidra.areyouok.permissions.PermissionItemUi
import com.anidra.areyouok.permissions.PermissionPrefs
import com.anidra.areyouok.permissions.PermissionStatusChecker
import com.anidra.areyouok.permissions.SettingsPermissionsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminderManager: CheckInReminderManager,
) : ViewModel() {

    private val checker = PermissionStatusChecker(context.applicationContext)
    private val prefs = PermissionPrefs(context.applicationContext)

    private val _uiState = MutableStateFlow(SettingsPermissionsUiState())
    val uiState: StateFlow<SettingsPermissionsUiState> = _uiState.asStateFlow()

    val remindersEnabled = reminderManager.remindersEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

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

    fun setCheckInRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            reminderManager.setEnabled(enabled)
        }
    }

    fun reconcileReminderSchedule() {
        viewModelScope.launch {
            reminderManager.reconcile()
        }
    }
}