package com.anidra.areyouok

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.anidra.areyouok.data.repositories.CheckInRepository
import com.anidra.areyouok.data.work.CheckInReminderWorkScheduler
import com.anidra.areyouok.data.work.CheckInWorkScheduler
import com.anidra.areyouok.data.work.EmergencyContactsWorkScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AreYouOkApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var checkInRepository: CheckInRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        CheckInWorkScheduler.ensurePeriodic(this)
        EmergencyContactsWorkScheduler.ensurePeriodic(this)

        // ✅ Reminder bootstrap: if checked in -> tomorrow, else next slot
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            if (checkInRepository.hasCheckedInToday()) {
                CheckInReminderWorkScheduler.scheduleTomorrowMorning(this@AreYouOkApp)
            } else {
                CheckInReminderWorkScheduler.scheduleNextFromNow(this@AreYouOkApp)
            }
        }
    }
}