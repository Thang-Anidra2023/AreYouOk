package com.anidra.areyouok.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.EmergencyContactsApi
import com.anidra.areyouok.data.network.dto.EmergencyContactRequest
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import com.anidra.areyouok.data.room.entity.EmergencyContactPendingOp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class EmergencyContactsSyncWorker @AssistedInject constructor(
    private val dao: EmergencyContactDao,
    private val api: EmergencyContactsApi,
    private val prefs: UserPrefs,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val token = prefs.authToken.first()
        if (token.isNullOrBlank()) return Result.retry()

        val pending = dao.getPending(limit = 50)
        if (pending.isEmpty()) return Result.success()

        var shouldRetry = false

        for (entity in pending) {
            val now = System.currentTimeMillis()
            val attempts = entity.attemptCount + 1

            try {
                val auth = "Bearer $token"

                when (EmergencyContactPendingOp.fromInt(entity.pendingOp)) {
                    EmergencyContactPendingOp.DELETE -> {
                        // If never created on server, delete locally
                        if (entity.serverId.isNullOrBlank()) {
                            dao.hardDelete(entity.localId)
                        } else {
                            api.delete(auth, entity.serverId)
                            dao.hardDelete(entity.localId)
                        }
                    }

                    EmergencyContactPendingOp.UPSERT,
                    EmergencyContactPendingOp.NONE -> {
                        // If UI edited but pendingOp accidentally NONE, still try to keep server consistent
                        val body = EmergencyContactRequest(
                            mobileNumber = entity.mobileNumber,
                            email = entity.email,
                            label = entity.label
                        )

                        val res = if (entity.serverId.isNullOrBlank()) {
                            api.add(auth, body)
                        } else {
                            api.update(auth, entity.serverId, body)
                        }

                        dao.markSynced(
                            localId = entity.localId,
                            serverId = res.id,
                            contactIndex = res.contactIndex,
                            verified = res.verified,
                            label = res.label,
                            syncedAtMillis = now,
                            attemptAtMillis = now,
                            attemptCount = attempts
                        )
                    }
                }
            } catch (e: IOException) {
                dao.markFailed(entity.localId, now, attempts, "Network: ${e.message ?: "IO"}")
                shouldRetry = true
            } catch (e: HttpException) {
                val code = e.code()
                val msg = "HTTP $code: ${e.message()}"

                // treat 404 on delete as success (already deleted)
                if (code == 404 && EmergencyContactPendingOp.fromInt(entity.pendingOp) == EmergencyContactPendingOp.DELETE) {
                    dao.hardDelete(entity.localId)
                } else {
                    dao.markFailed(entity.localId, now, attempts, msg)
                    if (code in 500..599) shouldRetry = true
                }
            } catch (e: Exception) {
                dao.markFailed(entity.localId, now, attempts, "Unexpected: ${e.message ?: e.javaClass.simpleName}")
                shouldRetry = true
            }
        }

        return if (shouldRetry) Result.retry() else Result.success()
    }
}