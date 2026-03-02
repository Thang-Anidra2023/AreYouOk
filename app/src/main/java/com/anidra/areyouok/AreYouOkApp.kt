package com.anidra.areyouok

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.anidra.areyouok.data.work.CheckInWorkScheduler
import com.anidra.areyouok.data.work.EmergencyContactsWorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AreYouOkApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        CheckInWorkScheduler.ensurePeriodic(this)
        EmergencyContactsWorkScheduler.ensurePeriodic(this)
    }
}