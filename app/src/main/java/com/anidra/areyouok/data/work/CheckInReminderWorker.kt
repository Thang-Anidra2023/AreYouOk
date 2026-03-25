package com.anidra.areyouok.data.work

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CheckInReminderWorker @AssistedInject constructor(
    private val reminderManager: CheckInReminderManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        if (reminderManager.shouldShowNotificationNow()) {
            CheckInReminderNotifier(applicationContext).show()
        }

        reminderManager.reconcile()
        return Result.success()
    }
}