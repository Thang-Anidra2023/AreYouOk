package com.anidra.areyouok.data.repositories

import android.content.Context
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.EmergencyContactsApi
import com.anidra.areyouok.data.network.dto.EmergencyContactRequest
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import com.anidra.areyouok.data.room.entity.EmergencyContactSyncState
import com.anidra.areyouok.data.work.EmergencyContactsWorkScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactsRepository @Inject constructor(
    private val dao: EmergencyContactDao,
    private val api: EmergencyContactsApi,
    private val prefs: UserPrefs,
    @ApplicationContext private val context: Context
) {

    private data class ContactFingerprint(
        val email: String,
        val phone: String,
        val label: String?
    )

    fun observeContacts(): Flow<List<EmergencyContactEntity>> = dao.observeActive()

    suspend fun addLocal(
        mobileNumber: String,
        email: String,
        label: String?
    ) {
        val sanitized = sanitizeLocalContacts()
        if (sanitized.size >= 3) {
            throw IllegalStateException("Maximum 3 emergency contacts allowed")
        }

        val now = System.currentTimeMillis()
        val entity = EmergencyContactEntity(
            localId = UUID.randomUUID().toString(),
            mobileNumber = normalizePhone(mobileNumber),
            email = normalizeEmail(email),
            label = normalizeLabel(label),
            syncState = EmergencyContactSyncState.PENDING.value,
            createdAtMillis = now,
            updatedAtMillis = now
        )

        dao.upsert(entity)
        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    suspend fun updateLocal(
        localId: String,
        mobileNumber: String,
        email: String,
        label: String?
    ) {
        val current = dao.getByLocalId(localId) ?: return
        val now = System.currentTimeMillis()

        dao.upsert(
            current.copy(
                mobileNumber = normalizePhone(mobileNumber),
                email = normalizeEmail(email),
                label = normalizeLabel(label),
                syncState = EmergencyContactSyncState.PENDING.value,
                updatedAtMillis = now,
                lastError = null
            )
        )

        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    suspend fun deleteLocal(localId: String) {
        dao.deleteByLocalId(localId)
        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    suspend fun refreshFromServer() {
        syncServerWithLocal(restoreFromServerWhenLocalEmpty = true)
    }

    /**
     * Plan B:
     * - If local is empty and server has data -> restore server into local.
     * - Otherwise local is source of truth.
     * - If local != server -> delete all server contacts, upload local contacts.
     */
    suspend fun syncServerWithLocal(
        restoreFromServerWhenLocalEmpty: Boolean = true
    ) {
        val token = prefs.authToken.first()
        if (token.isNullOrBlank()) return

        val auth = "Bearer $token"

        var local = sanitizeLocalContacts()
        val remote = api.list(auth)

        // Restore from server only when local is empty
        if (restoreFromServerWhenLocalEmpty && local.isEmpty() && remote.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val restored = remote
                .take(3)
                .map { item ->
                    EmergencyContactEntity(
                        localId = UUID.randomUUID().toString(),
                        mobileNumber = normalizePhone(item.mobileNumber),
                        email = normalizeEmail(item.email),
                        label = normalizeLabel(item.label),
                        contactIndex = item.contactIndex,
                        verified = item.verified,
                        syncState = EmergencyContactSyncState.SYNCED.value,
                        createdAtMillis = now,
                        updatedAtMillis = now,
                        syncedAtMillis = now,
                        lastAttemptAtMillis = now,
                        attemptCount = 0,
                        lastError = null
                    )
                }

            dao.replaceAll(restored)
            return
        }

        val localFingerprints = local
            .map { fingerprint(it.email, it.mobileNumber, it.label) }
            .sortedWith(compareBy({ it.email }, { it.phone }, { it.label ?: "" }))

        val remoteFingerprints = remote
            .map { fingerprint(it.email, it.mobileNumber, it.label) }
            .sortedWith(compareBy({ it.email }, { it.phone }, { it.label ?: "" }))

        // Already same -> just mark local as synced and refresh server metadata
        if (localFingerprints == remoteFingerprints) {
            val now = System.currentTimeMillis()
            val remoteByFingerprint = remote.associateBy {
                fingerprint(it.email, it.mobileNumber, it.label)
            }

            local.forEach { localItem ->
                val remoteItem = remoteByFingerprint[
                    fingerprint(localItem.email, localItem.mobileNumber, localItem.label)
                ]

                dao.markSynced(
                    localId = localItem.localId,
                    contactIndex = remoteItem?.contactIndex,
                    verified = remoteItem?.verified,
                    syncedAtMillis = now,
                    attemptAtMillis = now,
                    attemptCount = localItem.attemptCount
                )
            }
            return
        }

        try {
            local.forEach { item ->
                dao.setSyncState(
                    localId = item.localId,
                    syncState = EmergencyContactSyncState.PENDING.value,
                    updatedAtMillis = System.currentTimeMillis()
                )
            }

            // Delete everything from server first
            remote.forEach { serverItem ->
                api.delete(auth, serverItem.id)
            }

            // Upload local 3-contact source of truth
            local = sanitizeLocalContacts()
            for (localItem in local) {
                val response = api.add(
                    auth,
                    EmergencyContactRequest(
                        mobileNumber = localItem.mobileNumber,
                        email = localItem.email,
                        label = localItem.label
                    )
                )

                val now = System.currentTimeMillis()
                dao.markSynced(
                    localId = localItem.localId,
                    contactIndex = response.contactIndex,
                    verified = response.verified,
                    syncedAtMillis = now,
                    attemptAtMillis = now,
                    attemptCount = localItem.attemptCount + 1
                )
            }
        } catch (e: Exception) {
            markAllLocalFailed(errorMessage = toSyncErrorMessage(e))
            throw e
        }
    }

    /**
     * Cleans up bad old local state from the previous incremental-sync design.
     *
     * Priority:
     * 1. Keep FAILED/PENDING before SYNCED, because local unsynced edits should win.
     * 2. Prefer most recently updated rows.
     * 3. Keep max 3 rows.
     */
    private suspend fun sanitizeLocalContacts(): List<EmergencyContactEntity> {
        val current = dao.getAllOnce()

        val cleaned = current
            .sortedWith(
                compareBy<EmergencyContactEntity>(
                    { localPriority(it) },
                    { -it.updatedAtMillis }
                )
            )
            .distinctBy { fingerprint(it.email, it.mobileNumber, it.label) }
            .take(3)
            .sortedByDescending { it.updatedAtMillis }

        val currentIds = current.map { it.localId }
        val cleanedIds = cleaned.map { it.localId }

        if (currentIds != cleanedIds) {
            dao.replaceAll(cleaned)
        }

        return cleaned
    }

    private suspend fun markAllLocalFailed(errorMessage: String) {
        val now = System.currentTimeMillis()
        dao.getAllOnce().forEach { entity ->
            dao.markFailed(
                localId = entity.localId,
                attemptAtMillis = now,
                attemptCount = entity.attemptCount + 1,
                error = errorMessage
            )
        }
    }

    private fun localPriority(entity: EmergencyContactEntity): Int {
        return when (EmergencyContactSyncState.fromInt(entity.syncState)) {
            EmergencyContactSyncState.FAILED -> 0
            EmergencyContactSyncState.PENDING -> 1
            EmergencyContactSyncState.SYNCED -> 2
        }
    }

    private fun normalizeEmail(value: String): String =
        value.trim().lowercase()

    private fun normalizePhone(value: String): String =
        value.trim().filter { it.isDigit() || it == '+' }

    private fun normalizeLabel(value: String?): String? =
        value?.trim()?.ifBlank { null }

    private fun fingerprint(
        email: String,
        phone: String,
        label: String?
    ): ContactFingerprint {
        return ContactFingerprint(
            email = normalizeEmail(email),
            phone = normalizePhone(phone),
            label = normalizeLabel(label)
        )
    }

    private fun toSyncErrorMessage(e: Exception): String {
        return when (e) {
            is HttpException -> "HTTP ${e.code()}: ${e.message()}"
            is IOException -> "Network error: ${e.message ?: "IO error"}"
            else -> e.message ?: "Unknown sync error"
        }
    }
}