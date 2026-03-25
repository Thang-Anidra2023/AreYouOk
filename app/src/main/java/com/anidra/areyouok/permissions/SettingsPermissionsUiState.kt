package com.anidra.areyouok.permissions

data class SettingsPermissionsUiState(
    val notifications: PermissionItemUi = PermissionItemUi(),
    val location: PermissionItemUi = PermissionItemUi(),
    val motion: PermissionItemUi = PermissionItemUi()
)