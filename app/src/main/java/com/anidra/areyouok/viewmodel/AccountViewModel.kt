package com.anidra.areyouok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.repositories.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class AccountUiState(
    val isLoading: Boolean = false,
    val account: AccountProfile? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState(isLoading = true))
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        loadAccount()
    }

    fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            _uiState.value = try {
                val account = accountRepository.getAccountProfile()
                AccountUiState(
                    isLoading = false,
                    account = account,
                    errorMessage = null
                )
            } catch (e: IOException) {
                AccountUiState(
                    isLoading = false,
                    account = null,
                    errorMessage = "Network error. Please try again."
                )
            } catch (e: HttpException) {
                AccountUiState(
                    isLoading = false,
                    account = null,
                    errorMessage = "Server error ${e.code()}. Please try again."
                )
            } catch (e: Exception) {
                AccountUiState(
                    isLoading = false,
                    account = null,
                    errorMessage = e.message ?: "Something went wrong."
                )
            }
        }
    }
}