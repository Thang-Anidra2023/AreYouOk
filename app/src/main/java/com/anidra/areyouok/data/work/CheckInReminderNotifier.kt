package com.anidra.areyouok.data.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anidra.areyouok.R

class CheckInReminderNotifier(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "checkin_reminders"
        private const val CHANNEL_NAME = "Check-in reminders"
        private const val NOTIF_ID = 44001
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return

        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun show() {
        ensureChannel()

        // Open your app (launcher activity). No need to know your MainActivity class name.
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return

        val pi = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("AreYouOk")
            .setContentText("You haven’t checked in today. Tap to check in.")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notif)
    }
}