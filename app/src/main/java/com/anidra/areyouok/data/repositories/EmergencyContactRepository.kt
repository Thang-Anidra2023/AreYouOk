package com.anidra.areyouok.data.repositories


import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactRepository @Inject constructor(
    private val dao: EmergencyContactDao
) {
    val contacts: Flow<List<EmergencyContactEntity>> = dao.observeAll()

    suspend fun addContact(contact: EmergencyContactEntity) {
        val current = dao.count()
        if (current >= 3) throw IllegalStateException("Max 3 emergency contacts allowed.")
        dao.insert(contact)
    }

    suspend fun updateContact(contact: EmergencyContactEntity) {
        // no count check needed — you’re not increasing rows
        dao.update(contact)
    }

    suspend fun deleteContact(contact: EmergencyContactEntity) {
        dao.delete(contact)
    }
}