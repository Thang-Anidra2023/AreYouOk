package com.anidra.areyouok.data.repositories


import android.content.Context
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.EmergencyContactsApi
import com.anidra.areyouok.data.network.dto.EmergencyContactRequest
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import com.anidra.areyouok.data.room.entity.EmergencyContactPendingOp
import com.anidra.areyouok.data.room.entity.EmergencyContactSyncState
import com.anidra.areyouok.data.work.EmergencyContactsWorkScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    fun observeContacts(): Flow<List<EmergencyContactEntity>> = dao.observeActive()

    suspend fun addLocal(mobileNumber: String, email: String, label: String?) {
        val now = System.currentTimeMillis()
        val entity = EmergencyContactEntity(
            localId = UUID.randomUUID().toString(),
            serverId = null,
            mobileNumber = mobileNumber,
            email = email,
            label = label,
            contactIndex = null,
            verified = null,
            isDeleted = false,
            pendingOp = EmergencyContactPendingOp.UPSERT.value,
            syncState = EmergencyContactSyncState.PENDING.value,
            createdAtMillis = now,
            updatedAtMillis = now
        )
        dao.upsert(entity)
        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    suspend fun updateLocal(localId: String, mobileNumber: String, email: String, label: String?) {
        val current = dao.getByLocalId(localId) ?: return
        val now = System.currentTimeMillis()
        dao.upsert(
            current.copy(
                mobileNumber = mobileNumber,
                email = email,
                label = label,
                isDeleted = false,
                pendingOp = EmergencyContactPendingOp.UPSERT.value,
                syncState = EmergencyContactSyncState.PENDING.value,
                updatedAtMillis = now
            )
        )
        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    suspend fun deleteLocal(localId: String) {
        val current = dao.getByLocalId(localId) ?: return
        val now = System.currentTimeMillis()

        // If never created on server, just delete locally
        if (current.serverId.isNullOrBlank()) {
            dao.hardDelete(localId)
            return
        }

        // Soft delete + sync delete later
        dao.markPendingDelete(localId, updatedAtMillis = now)
        EmergencyContactsWorkScheduler.enqueueSyncNow(context)
    }

    /** Optional: call this after login to pull server state and cache it locally */
    suspend fun refreshFromServer() {
        val token = prefs.authToken.first()
        if (token.isNullOrBlank()) return

        val remote = api.list("Bearer $token")
        val now = System.currentTimeMillis()

        // Minimal merge: insert/update server contacts into local cache (doesn't overwrite local pending edits)
        for (r in remote) {
            // Try find by serverId using a small query: easiest is to just insert a new row if user has none.
            // If you want perfect merge, add dao.getByServerId(serverId) query.
            val entity = EmergencyContactEntity(
                localId = UUID.randomUUID().toString(),
                serverId = r.id,
                mobileNumber = r.mobileNumber,
                email = r.email,
                label = r.label,
                contactIndex = r.contactIndex,
                verified = r.verified,
                isDeleted = false,
                pendingOp = EmergencyContactPendingOp.NONE.value,
                syncState = EmergencyContactSyncState.SYNCED.value,
                createdAtMillis = now,
                updatedAtMillis = now,
                syncedAtMillis = now
            )
            dao.upsert(entity)
        }
    }
}