package com.anidra.areyouok.data.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object CheckInReminderWorkScheduler {

    private const val UNIQUE_WORK = "checkin_reminder_one_time"

    fun scheduleNextFromNow(context: Context) {
        enqueueAt(context, CheckInReminderSchedule.nextSlotAfter(CheckInReminderSchedule.now()))
    }

    fun scheduleTomorrowMorning(context: Context) {
        enqueueAt(context, CheckInReminderSchedule.tomorrowMorning(CheckInReminderSchedule.now()))
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
    }

    private fun enqueueAt(context: Context, runAt: ZonedDateTime) {
        val now = CheckInReminderSchedule.now()
        val delayMs = Duration.between(now, runAt).toMillis().coerceAtLeast(0)

        val request = OneTimeWorkRequestBuilder<CheckInReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("checkin_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}