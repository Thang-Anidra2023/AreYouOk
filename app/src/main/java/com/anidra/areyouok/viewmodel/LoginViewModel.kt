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

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String) {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.value = LoginUiState(loading = true)

            try {
                repo.login(email, password)
                _state.value = LoginUiState(success = true)
            } catch (e: HttpException) {
                _state.value = LoginUiState(error = "HTTP ${e.code()}: ${e.message()}")
            } catch (e: IOException) {
                _state.value = LoginUiState(error = "Network error: ${e.message}")
            } catch (e: Exception) {
                _state.value = LoginUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}