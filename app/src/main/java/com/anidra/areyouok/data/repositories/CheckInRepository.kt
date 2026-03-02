package com.anidra.areyouok.data.repositories

import android.content.Context
import com.anidra.areyouok.data.room.dao.CheckInDao
import com.anidra.areyouok.data.room.entity.CheckInEntity
import com.anidra.areyouok.data.room.entity.CheckInSyncState
import com.anidra.areyouok.data.work.CheckInReminderWorkScheduler
import com.anidra.areyouok.data.work.CheckInWorkScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepository @Inject constructor(
    private val dao: CheckInDao,
    @ApplicationContext private val context: Context
) {
    private fun todayEpochDay(): Long =
        LocalDate.now(ZoneId.systemDefault()).toEpochDay()

    fun observeToday(): Flow<CheckInEntity?> =
        dao.observeByDay(todayEpochDay())

    suspend fun hasCheckedInToday(): Boolean {
        return dao.getByDay(todayEpochDay()) != null
    }

    suspend fun checkInToday() {
        val epochDay = todayEpochDay()
        val existing = dao.getByDay(epochDay)

        // If already synced today, don't create duplicates — but still schedule tomorrow reminder.
        if (existing != null && existing.syncState == CheckInSyncState.SYNCED.value) {
            CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
            return
        }

        val now = System.currentTimeMillis()
        val tz = ZoneId.systemDefault().id

        val entity = (
                existing?.copy(
                    timeZoneId = tz,
                    syncState = CheckInSyncState.PENDING.value,
                    lastError = null
                )
                    ?: CheckInEntity(
                        epochDay = epochDay,
                        createdAtMillis = now,
                        timeZoneId = tz,
                        syncState = CheckInSyncState.PENDING.value
                    )
                )

        dao.upsert(entity)

        // Trigger background upload
        CheckInWorkScheduler.enqueueSyncNow(context)

        // ✅ User checked in today -> reminders tomorrow morning
        CheckInReminderWorkScheduler.scheduleTomorrowMorning(context)
    }

    fun retrySyncNow() {
        CheckInWorkScheduler.enqueueSyncNow(context)
    }
}