package com.anidra.areyouok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val loading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val loggingOut: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val prefs: UserPrefs,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val logoutInProgress = MutableStateFlow(false)

    val uiState: StateFlow<SessionUiState> = combine(
        prefs.authToken,
        logoutInProgress
    ) { token, loggingOut ->
        SessionUiState(
            loading = false,
            isLoggedIn = !token.isNullOrBlank(),
            loggingOut = loggingOut
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionUiState()
    )

    fun logout() {
        if (logoutInProgress.value) return

        viewModelScope.launch {
            logoutInProgress.value = true
            try {
                authRepository.logout(clearLocalData = true)
            } finally {
                logoutInProgress.value = false
            }
        }
    }
}