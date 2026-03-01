package com.anidra.areyouok.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.CheckInApi
import com.anidra.areyouok.data.network.dto.CheckInCreateRequest
import com.anidra.areyouok.data.room.dao.CheckInDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

@HiltWorker
class CheckInSyncWorker @AssistedInject constructor(
    private val dao: CheckInDao,
    private val api: CheckInApi,
    private val prefs: UserPrefs,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val token = prefs.authToken.first()
        if (token.isNullOrBlank()) {
            // No session yet -> try again later
            return Result.retry()
        }

        val pending = dao.getNotSynced(limit = 50)
        if (pending.isEmpty()) return Result.success()

        var shouldRetry = false

        for (entity in pending) {
            val now = System.currentTimeMillis()
            val attempts = entity.attemptCount + 1

            try {
                val date = LocalDate.ofEpochDay(entity.epochDay).toString() // yyyy-MM-dd

                val res = api.createCheckIn(
                    authorization = "Bearer $token",
                    idempotencyKey = "checkin-${entity.epochDay}",
                    body = CheckInCreateRequest(
                        epochDay = entity.epochDay,
                        date = date,
                        timeZoneId = entity.timeZoneId,
                        createdAtMillis = entity.createdAtMillis
                    )
                )

                dao.markSynced(
                    epochDay = entity.epochDay,
                    serverId = res.id,
                    syncedAtMillis = now,
                    attemptAtMillis = now,
                    attemptCount = attempts
                )
            } catch (e: IOException) {
                // network issue -> retry whole worker
                dao.markFailed(
                    epochDay = entity.epochDay,
                    attemptAtMillis = now,
                    attemptCount = attempts,
                    error = "Network error: ${e.message ?: "IO"}"
                )
                shouldRetry = true
            } catch (e: HttpException) {
                // server responded
                val code = e.code()
                val msg = "HTTP $code: ${e.message()}"

                // If server supports idempotency, duplicates won't happen.
                // If not, you might see 409 conflict for same day -> treat as success.
                if (code == 409) {
                    dao.markSynced(
                        epochDay = entity.epochDay,
                        serverId = entity.serverId, // keep whatever
                        syncedAtMillis = now,
                        attemptAtMillis = now,
                        attemptCount = attempts
                    )
                } else {
                    dao.markFailed(
                        epochDay = entity.epochDay,
                        attemptAtMillis = now,
                        attemptCount = attempts,
                        error = msg
                    )
                    if (code in 500..599) shouldRetry = true
                }
            } catch (e: Exception) {
                dao.markFailed(
                    epochDay = entity.epochDay,
                    attemptAtMillis = now,
                    attemptCount = attempts,
                    error = "Unexpected: ${e.message ?: e.javaClass.simpleName}"
                )
                shouldRetry = true
            }
        }

        return if (shouldRetry) Result.retry() else Result.success()
    }
}