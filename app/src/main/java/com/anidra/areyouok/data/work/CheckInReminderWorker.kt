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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CheckInReminderWorker @AssistedInject constructor(
    private val checkInRepository: CheckInRepository,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = CheckInReminderSchedule.now()

        // ✅ If checked in today -> reminder tomorrow morning
        if (checkInRepository.hasCheckedInToday()) {
            CheckInReminderWorkScheduler.scheduleTomorrowMorning(applicationContext)
            return Result.success()
        }

        // ✅ Outside 8am–8pm -> reminder tomorrow morning
        if (!CheckInReminderSchedule.isWithinWindow(now)) {
            CheckInReminderWorkScheduler.scheduleTomorrowMorning(applicationContext)
            return Result.success()
        }

        // ✅ Not checked in + within window: notify (if allowed)
        if (canPostNotifications()) {
            CheckInReminderNotifier(applicationContext).show()
        }

        // ✅ Schedule next slot today (or tomorrow 8am if slots are done)
        CheckInReminderWorkScheduler.scheduleNextFromNow(applicationContext)
        return Result.success()
    }

    private fun canPostNotifications(): Boolean {
        if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) return false

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