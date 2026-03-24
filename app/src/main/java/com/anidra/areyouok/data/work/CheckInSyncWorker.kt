package com.anidra.areyouok.data.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anidra.areyouok.data.manager.SessionManager
import com.anidra.areyouok.data.network.CheckInApi
import com.anidra.areyouok.data.network.dto.CheckInRequest
import com.anidra.areyouok.data.room.dao.CheckInDao
import com.anidra.areyouok.data.session.SessionExpiredException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class CheckInSyncWorker @AssistedInject constructor(
    private val dao: CheckInDao,
    private val api: CheckInApi,
    private val sessionManager: SessionManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val pending = dao.getNotSynced(limit = 50)
        if (pending.isEmpty()) return Result.success()

        var shouldRetry = false

        for (entity in pending) {
            if (isStopped) return Result.success()

            val now = System.currentTimeMillis()
            val attempts = entity.attemptCount + 1

            try {
                sessionManager.withAuthRetry { auth ->
                    val res = api.checkIn(
                        authorization = auth,
                        body = CheckInRequest(snoozeDays = null)
                    )
                    Log.i("CheckInSyncWorker", "Server response message = ${res.message}")
                }

                dao.markSynced(
                    epochDay = entity.epochDay,
                    serverId = entity.serverId,
                    syncedAtMillis = now,
                    attemptAtMillis = now,
                    attemptCount = attempts
                )
            } catch (e: SessionExpiredException) {
                dao.markFailed(
                    epochDay = entity.epochDay,
                    attemptAtMillis = now,
                    attemptCount = attempts,
                    error = e.message ?: "Session expired. Please log in again."
                )
                return Result.success()
            } catch (e: IOException) {
                dao.markFailed(
                    epochDay = entity.epochDay,
                    attemptAtMillis = now,
                    attemptCount = attempts,
                    error = "Network error: ${e.message ?: "IO"}"
                )
                shouldRetry = true
            } catch (e: HttpException) {
                val code = e.code()
                val msg = "HTTP $code: ${e.message()}"

                if (code == 409) {
                    dao.markSynced(
                        epochDay = entity.epochDay,
                        serverId = entity.serverId,
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