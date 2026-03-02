package com.anidra.areyouok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.repositories.CheckInRepository
import com.anidra.areyouok.data.repositories.EmergencyContactsRepository
import com.anidra.areyouok.data.room.entity.CheckInSyncState
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckInUiState(
    val contacts: List<EmergencyContactEntity> = emptyList(),
    val canAddMore: Boolean = true,

    val checkedInToday: Boolean = false,
    val syncState: CheckInSyncState = CheckInSyncState.PENDING
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val contactsRepo: EmergencyContactsRepository,
    private val checkInRepo: CheckInRepository
) : ViewModel() {

    val uiState: StateFlow<CheckInUiState> =
        combine(
            contactsRepo.observeContacts(),
            checkInRepo.observeToday()
        ) { contacts, today ->
            val sync = today?.syncState?.let { CheckInSyncState.fromInt(it) } ?: CheckInSyncState.PENDING
            CheckInUiState(
                contacts = contacts,
                canAddMore = contacts.size < 3,
                checkedInToday = today != null,
                syncState = sync
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CheckInUiState()
        )

    fun checkInToday(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                checkInRepo.checkInToday()
            } catch (e: Exception) {
                onError(e.message ?: "Could not check in")
            }
        }
    }

    fun retrySync() {
        checkInRepo.retrySyncNow()
    }

    fun addContact(label: String, email: String, mobileNumber: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                contactsRepo.addLocal(
                    mobileNumber = mobileNumber.trim(),
                    email = email.trim(),
                    label = label.trim().ifBlank { null }
                )
            } catch (e: Exception) {
                onError(e.message ?: "Could not save contact")
            }
        }
    }

    fun updateContact(localId: String, label: String, email: String, mobileNumber: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                contactsRepo.updateLocal(
                    localId = localId,
                    mobileNumber = mobileNumber.trim(),
                    email = email.trim(),
                    label = label.trim().ifBlank { null }
                )
            } catch (e: Exception) {
                onError(e.message ?: "Could not update contact")
            }
        }
    }

    fun deleteContact(contact: EmergencyContactEntity) {
        viewModelScope.launch {
            contactsRepo.deleteLocal(contact.localId)
        }
    }
}