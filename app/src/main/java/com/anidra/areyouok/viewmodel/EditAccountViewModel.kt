package com.anidra.areyouok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.model.AlertChannelPreference
import com.anidra.areyouok.data.repositories.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class EditAccountUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val mobileNumber: String = "",
    val inactivityThresholdDays: String = "",
    val alertChannelPreference: AlertChannelPreference = AlertChannelPreference.BOTH,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAccountUiState())
    val uiState: StateFlow<EditAccountUiState> = _uiState.asStateFlow()

    private val phoneRegex = Regex("^[0-9+\\-()\\s]+$")

    init {
        loadAccount()
    }

    fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val account = accountRepository.getAccountProfile()
                _uiState.value = account.toEditUiState()
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error. Please try again."
                )
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Server error ${e.code()}. Please try again."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Something went wrong."
                )
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null, successMessage = null)
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, errorMessage = null, successMessage = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, errorMessage = null, successMessage = null)
    }

    fun onMobileNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = value, errorMessage = null, successMessage = null)
    }

    fun onInactivityThresholdDaysChange(value: String) {
        _uiState.value = _uiState.value.copy(
            inactivityThresholdDays = value.filter { it.isDigit() },
            errorMessage = null,
            successMessage = null
        )
    }

    fun onAlertChannelPreferenceChange(value: AlertChannelPreference) {
        _uiState.value = _uiState.value.copy(
            alertChannelPreference = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveChanges() {
        val current = _uiState.value
        if (current.isSaving || current.isLoading) return

        val email = current.email.trim()
        val firstName = current.firstName.trim()
        val lastName = current.lastName.trim()
        val mobileNumber = current.mobileNumber.trim()
        val thresholdText = current.inactivityThresholdDays.trim()

        if (email.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Email is required")
            return
        }

        val parsedThreshold = when {
            thresholdText.isBlank() -> null
            thresholdText.toIntOrNull() == null -> {
                _uiState.value = current.copy(errorMessage = "Inactivity threshold must be a number")
                return
            }
            else -> thresholdText.toInt()
        }

        if (parsedThreshold != null && parsedThreshold !in 1..90) {
            _uiState.value = current.copy(errorMessage = "Inactivity threshold must be between 1 and 90")
            return
        }

        if (mobileNumber.isNotBlank()) {
            if (mobileNumber.length !in 6..20) {
                _uiState.value = current.copy(errorMessage = "Mobile number must be 6 to 20 characters")
                return
            }
            if (!phoneRegex.matches(mobileNumber)) {
                _uiState.value = current.copy(errorMessage = "Mobile number has invalid characters")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = current.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val updated = accountRepository.updateAccountProfile(
                    email = email,
                    inactivityThresholdDays = parsedThreshold,
                    alertChannelPreference = current.alertChannelPreference,
                    firstName = firstName,
                    lastName = lastName,
                    mobileNumber = mobileNumber
                )

                _uiState.value = updated.toEditUiState().copy(
                    isSaving = false,
                    successMessage = "Profile updated successfully.",
                    isSaved = true
                )
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Network error. Please try again."
                )
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Server error ${e.code()}. Please try again."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Something went wrong."
                )
            }
        }
    }

    fun consumeSavedEvent() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}

private fun AccountProfile.toEditUiState(): EditAccountUiState {
    return EditAccountUiState(
        isLoading = false,
        isSaving = false,
        email = email,
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber.orEmpty(),
        inactivityThresholdDays = inactivityThresholdDays?.toString().orEmpty(),
        alertChannelPreference = alertChannelPreference,
        errorMessage = null,
        successMessage = null,
        isSaved = false
    )
}