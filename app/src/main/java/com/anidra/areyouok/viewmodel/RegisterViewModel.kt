package com.anidra.areyouok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class RegisterUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

data class RegisterForm(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val registrationType: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val country: String = "",
    val state: String = "",
    val mobileNumber: String = "",
    val addressLine1: String = "",
    val addressLine2: String = ""
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun register(form: RegisterForm) {
        if (_state.value.loading) return

        val email = form.email.trim()
        val password = form.password
        val confirmPassword = form.confirmPassword

        when {
            email.isBlank() -> {
                _state.value = RegisterUiState(error = "Email is required")
                return
            }

            password.isBlank() -> {
                _state.value = RegisterUiState(error = "Password is required")
                return
            }

            password.length < 8 -> {
                _state.value = RegisterUiState(error = "Password must be at least 8 characters")
                return
            }

            password != confirmPassword -> {
                _state.value = RegisterUiState(error = "Passwords do not match")
                return
            }
        }

        viewModelScope.launch {
            _state.value = RegisterUiState(loading = true)

            try {
                repo.register(
                    email = email,
                    password = password,
                    registrationType = form.registrationType.nullIfBlank(),
                    firstName = form.firstName.nullIfBlank(),
                    middleName = form.middleName.nullIfBlank(),
                    lastName = form.lastName.nullIfBlank(),
                    country = form.country.nullIfBlank(),
                    state = form.state.nullIfBlank(),
                    mobileNumber = form.mobileNumber.nullIfBlank(),
                    addressLine1 = form.addressLine1.nullIfBlank(),
                    addressLine2 = form.addressLine2.nullIfBlank()
                )
                _state.value = RegisterUiState(success = true)
            } catch (e: HttpException) {
                _state.value = RegisterUiState(
                    error = "HTTP ${e.code()}: ${e.message()}"
                )
            } catch (e: IOException) {
                _state.value = RegisterUiState(
                    error = "Network error: ${e.message}"
                )
            } catch (e: Exception) {
                _state.value = RegisterUiState(
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun String.nullIfBlank(): String? =
        trim().takeIf { it.isNotEmpty() }
}