package com.anidra.areyouok.permissions

enum class PermissionState {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED,
    NOT_REQUIRED,
    UNSUPPORTED
}

data class PermissionItemUi(
    val state: PermissionState = PermissionState.DENIED
) {
    val isGranted: Boolean
        get() = state == PermissionState.GRANTED || state == PermissionState.NOT_REQUIRED

    val statusText: String
        get() = when (state) {
            PermissionState.GRANTED -> "Enabled"
            PermissionState.NOT_REQUIRED -> "Enabled"
            PermissionState.DENIED -> "Enable"
            PermissionState.PERMANENTLY_DENIED -> "Settings"
            PermissionState.UNSUPPORTED -> "Unsupported"
        }
}