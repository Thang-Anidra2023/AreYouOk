package com.anidra.areyouok.data.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anidra.areyouok.data.repositories.CheckInRepository
import com.anidra.areyouok.data.repositories.ReminderSettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CheckInReminderWorker @AssistedInject constructor(
    private val checkInRepository: CheckInRepository,
    private val reminderSettingsRepository: ReminderSettingsRepository,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!reminderSettingsRepository.isCheckInRemindersEnabled()) {
            return Result.success()
        }

        val now = CheckInReminderSchedule.now()

        if (checkInRepository.hasCheckedInToday()) {
            CheckInReminderWorkScheduler.scheduleTomorrowMorning(applicationContext)
            return Result.success()
        }

        if (!CheckInReminderSchedule.isWithinWindow(now)) {
            CheckInReminderWorkScheduler.scheduleTomorrowMorning(applicationContext)
            return Result.success()
        }

        if (canPostNotifications()) {
            CheckInReminderNotifier(applicationContext).show()
        }

        // Re-check in case user switched it off while this worker was running
        if (reminderSettingsRepository.isCheckInRemindersEnabled()) {
            CheckInReminderWorkScheduler.scheduleNextFromNow(applicationContext)
        }

        return Result.success()
    }

    private fun canPostNotifications(): Boolean {
        if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            return false
        }

        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}