package com.anidra.areyouok.data.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

object CheckInReminderWorkScheduler {

    private const val UNIQUE_ONE_TIME = "checkin_reminder_one_time"

    fun scheduleNextFromNow(context: Context) {
        val now = CheckInReminderSchedule.now()
        enqueueAt(context, CheckInReminderSchedule.nextSlotAfter(now))
    }

    fun scheduleTomorrowMorning(context: Context) {
        val now = CheckInReminderSchedule.now()
        enqueueAt(context, CheckInReminderSchedule.tomorrowMorning(now))
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_ONE_TIME)
    }

    private fun enqueueAt(context: Context, runAt: java.time.ZonedDateTime) {
        val now = CheckInReminderSchedule.now()
        val delayMs = Duration.between(now, runAt).toMillis().coerceAtLeast(0)

        val req = OneTimeWorkRequestBuilder<CheckInReminderWorker>()
            .addTag("checkin_reminder")
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            req
        )
        android.util.Log.d(
            "CheckInReminder",
            "Scheduled reminder at=$runAt now=$now delayMs=$delayMs"
        )
    }

    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_ONE_TIME)
    }
}