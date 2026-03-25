package com.anidra.areyouok.data.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.anidra.areyouok.data.repositories.ReminderSettingsRepository
import com.anidra.areyouok.data.room.dao.CheckInDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInReminderManager @Inject constructor(
    private val reminderSettingsRepository: ReminderSettingsRepository,
    private val checkInDao: CheckInDao,
    @ApplicationContext private val context: Context
) {
    val remindersEnabled: Flow<Boolean> = reminderSettingsRepository.checkInRemindersEnabled

    suspend fun setEnabled(enabled: Boolean) {
        reminderSettingsRepository.setCheckInRemindersEnabled(enabled)
        reconcile()
    }

    suspend fun isEnabled(): Boolean {
        return reminderSettingsRepository.isCheckInRemindersEnabled()
    }

    suspend fun shouldShowNotificationNow(): Boolean {
        if (!isEnabled()) return false
        if (!canPostNotifications()) return false
        if (hasCheckedInToday()) return false
        return CheckInReminderSchedule.isWithinWindow(CheckInReminderSchedule.now())
    }

    suspend fun reconcile() {
        if (!isEnabled() || !canPostNotifications()) {
            CheckInReminderWorkScheduler.cancel(context)
            return
        }

        val now = CheckInReminderSchedule.now()

        when {
            hasCheckedInToday() -> {
                CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
            }
            CheckInReminderSchedule.isWithinWindow(now) -> {
                CheckInReminderWorkScheduler.scheduleNextFromNow(context)
            }
            else -> {
                CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
            }
        }
    }

    private suspend fun hasCheckedInToday(): Boolean {
        return checkInDao.getByDay(todayEpochDay()) != null
    }

    private fun todayEpochDay(): Long {
        return LocalDate.now(ZoneId.systemDefault()).toEpochDay()
    }

    private fun canPostNotifications(): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }

        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}