package com.anidra.areyouok.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.model.AlertChannelPreference
import com.anidra.areyouok.viewmodel.AccountViewModel



@Composable
fun AccountRoute(
    viewModel: AccountViewModel = hiltViewModel(),
    onEditProfile: () -> Unit = {},
    refreshOnReturn: Boolean = false,
    onRefreshConsumed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(refreshOnReturn) {
        if (refreshOnReturn) {
            viewModel.loadAccount()
            onRefreshConsumed()
        }
    }

    when {
        uiState.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.errorMessage ?: "Unknown error",
                    style = MaterialTheme.typography.bodyLarge
                )
                TextButton(onClick = viewModel::loadAccount) {
                    Text("Retry")
                }
            }
        }

        uiState.account != null -> {
            AccountInfoScreen(
                account = uiState.account!!.toUiModel(),
                onEditProfile = onEditProfile
            )
        }
    }
}

private fun AccountProfile.toUiModel(): AccountUiModel {
    return AccountUiModel(
        id = id,
        email = email,
        createdAt = createdAt,
        lastLoginDate = lastLoginDate,
        inactivityThresholdDays = inactivityThresholdDays,
        alertChannelPreference = when (alertChannelPreference) {
            AlertChannelPreference.EMAIL -> com.anidra.areyouok.ui.AlertChannelPreference.EMAIL
            AlertChannelPreference.SMS -> com.anidra.areyouok.ui.AlertChannelPreference.SMS
            AlertChannelPreference.BOTH -> com.anidra.areyouok.ui.AlertChannelPreference.BOTH
        },
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber
    )
}