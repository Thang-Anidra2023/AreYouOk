package com.anidra.areyouok.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.anidra.areyouok.permissions.PermissionItemUi
import com.anidra.areyouok.permissions.PermissionPrefs
import com.anidra.areyouok.permissions.PermissionStatusChecker
import com.anidra.areyouok.permissions.SettingsPermissionsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.repositories.CheckInRepository
import com.anidra.areyouok.data.repositories.ReminderSettingsRepository
import com.anidra.areyouok.data.work.CheckInReminderSchedule
import com.anidra.areyouok.data.work.CheckInReminderWorkScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminderSettingsRepository: ReminderSettingsRepository,
    private val checkInRepository: CheckInRepository,
) : ViewModel() {

    private val checker = PermissionStatusChecker(context.applicationContext)
    private val prefs = PermissionPrefs(context.applicationContext)

    private val _uiState = MutableStateFlow(SettingsPermissionsUiState())
    val uiState: StateFlow<SettingsPermissionsUiState> = _uiState.asStateFlow()

    val remindersEnabled = reminderSettingsRepository.checkInRemindersEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
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
            reminderSettingsRepository.setCheckInRemindersEnabled(enabled)

            if (enabled) {
                val now = CheckInReminderSchedule.now()
                when {
                    checkInRepository.hasCheckedInToday() -> {
                        CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
                    }
                    CheckInReminderSchedule.isWithinWindow(now) -> {
                        CheckInReminderWorkScheduler.scheduleNextFromNow(context)
                    }
                    else -> {
                        CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
                    }
                }
            } else {
                CheckInReminderWorkScheduler.cancel(context)
            }
        }
    }
}