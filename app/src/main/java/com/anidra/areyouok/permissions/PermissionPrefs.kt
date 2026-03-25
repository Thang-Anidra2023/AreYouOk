package com.anidra.areyouok.permissions

import android.content.Context

class PermissionPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("permission_prefs", Context.MODE_PRIVATE)

    fun wasAsked(key: String): Boolean = prefs.getBoolean(key, false)

    fun markAsked(key: String) {
        prefs.edit().putBoolean(key, true).apply()
    }

    companion object {
        const val NOTIFICATIONS = "notifications"
        const val LOCATION = "location"
        const val MOTION = "motion"
    }
}