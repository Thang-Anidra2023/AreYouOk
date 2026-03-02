package com.anidra.areyouok.data.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object EmergencyContactsWorkScheduler {
    private const val UNIQUE_ONE_TIME = "emergency_contacts_sync_one_time"
    private const val UNIQUE_PERIODIC = "emergency_contacts_sync_periodic"

    fun enqueueSyncNow(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = OneTimeWorkRequestBuilder<EmergencyContactsSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun ensurePeriodic(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val req = PeriodicWorkRequestBuilder<EmergencyContactsSyncWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            req
        )
    }
}