package com.anidra.areyouok.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anidra.areyouok.components.AuthBackground
import com.anidra.areyouok.components.AuthColors
import com.anidra.areyouok.components.AuthLabel
import com.anidra.areyouok.components.AuthPrimaryButton
import com.anidra.areyouok.components.GlassCard
import com.anidra.areyouok.components.GlassyTextField
import com.anidra.areyouok.data.model.AlertChannelPreference
import com.anidra.areyouok.viewmodel.EditAccountUiState
import com.anidra.areyouok.viewmodel.EditAccountViewModel

@Composable
fun EditAccountRoute(
    viewModel: EditAccountViewModel = hiltViewModel(),
    onSaved: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.consumeSavedEvent()
            onSaved()
        }
    }

    EditAccountScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onMobileNumberChange = viewModel::onMobileNumberChange,
        onInactivityThresholdDaysChange = viewModel::onInactivityThresholdDaysChange,
        onAlertChannelPreferenceChange = viewModel::onAlertChannelPreferenceChange,
        onSaveClick = viewModel::saveChanges,
        onRetry = viewModel::loadAccount,
        onBack = onBack
    )
}

@Composable
fun EditAccountScreen(
    uiState: EditAccountUiState,
    onEmailChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit,
    onInactivityThresholdDaysChange: (String) -> Unit,
    onAlertChannelPreferenceChange: (AlertChannelPreference) -> Unit,
    onSaveClick: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    AuthBackground(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Profile",
                            color = AuthColors.Title,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        TextButton(onClick = onBack) {
                            Text("Cancel", color = AuthColors.AccentOrange)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            AuthLabel("Email")
                            Spacer(Modifier.height(10.dp))
                            GlassyTextField(
                                value = uiState.email,
                                onValueChange = onEmailChange,
                                placeholder = "Enter your email",
                                leadingIcon = Icons.Outlined.Email,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(Modifier.height(18.dp))

                            AuthLabel("First Name")
                            Spacer(Modifier.height(10.dp))
                            GlassyTextField(
                                value = uiState.firstName,
                                onValueChange = onFirstNameChange,
                                placeholder = "Enter your first name",
                                leadingIcon = Icons.Outlined.Person
                            )

                            Spacer(Modifier.height(18.dp))

                            AuthLabel("Last Name")
                            Spacer(Modifier.height(10.dp))
                            GlassyTextField(
                                value = uiState.lastName,
                                onValueChange = onLastNameChange,
                                placeholder = "Enter your last name",
                                leadingIcon = Icons.Outlined.Person
                            )

                            Spacer(Modifier.height(18.dp))

                            AuthLabel("Mobile Number")
                            Spacer(Modifier.height(10.dp))
                            GlassyTextField(
                                value = uiState.mobileNumber,
                                onValueChange = onMobileNumberChange,
                                placeholder = "Enter your mobile number",
                                leadingIcon = Icons.Outlined.Call,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )

                            Spacer(Modifier.height(18.dp))

                            AuthLabel("Inactivity Threshold (days)")
                            Spacer(Modifier.height(10.dp))
                            GlassyTextField(
                                value = uiState.inactivityThresholdDays,
                                onValueChange = onInactivityThresholdDaysChange,
                                placeholder = "1 to 90",
                                leadingIcon = Icons.Outlined.Timer,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(Modifier.height(18.dp))

                            AuthLabel("Alert Channel")
                            Spacer(Modifier.height(10.dp))
                            AlertChannelSelector(
                                selected = uiState.alertChannelPreference,
                                onSelected = onAlertChannelPreferenceChange
                            )

                            uiState.errorMessage?.let {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            uiState.successMessage?.let {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = it,
                                    color = Color(0xFF81C784),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(22.dp))

                            AuthPrimaryButton(
                                text = if (uiState.isSaving) "Saving..." else "Save Changes",
                                icon = Icons.Outlined.Save,
                                onClick = onSaveClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                            )

                            if (uiState.isSaving) {
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            TextButton(
                                onClick = onRetry,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Reload current values", color = AuthColors.AccentOrange)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun AlertChannelSelector(
    selected: AlertChannelPreference,
    onSelected: (AlertChannelPreference) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlertChannelButton(
            modifier = Modifier.weight(1f),
            text = "Email",
            selected = selected == AlertChannelPreference.EMAIL,
            onClick = { onSelected(AlertChannelPreference.EMAIL) }
        )
        AlertChannelButton(
            modifier = Modifier.weight(1f),
            text = "SMS",
            selected = selected == AlertChannelPreference.SMS,
            onClick = { onSelected(AlertChannelPreference.SMS) }
        )
        AlertChannelButton(
            modifier = Modifier.weight(1f),
            text = "Both",
            selected = selected == AlertChannelPreference.BOTH,
            onClick = { onSelected(AlertChannelPreference.BOTH) }
        )
    }
}

@Composable
private fun AlertChannelButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) AuthColors.AccentOrange else AuthColors.GlassBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) AuthColors.ButtonFill else Color.White.copy(alpha = 0.06f),
            contentColor = Color.White
        )
    ) {
        Text(text = text)
    }
}