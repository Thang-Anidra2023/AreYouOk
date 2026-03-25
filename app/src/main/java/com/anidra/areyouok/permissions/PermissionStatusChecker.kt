package com.anidra.areyouok.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class PermissionStatusChecker(
    private val context: Context
) {

    fun notificationState(
        activity: Activity?,
        askedBefore: Boolean
    ): PermissionState {
        val appNotificationsEnabled =
            NotificationManagerCompat.from(context).areNotificationsEnabled()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return if (appNotificationsEnabled) {
                PermissionState.NOT_REQUIRED
            } else {
                PermissionState.DENIED
            }
        }

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        return when {
            granted && appNotificationsEnabled -> PermissionState.GRANTED
            activity != null &&
                    askedBefore &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) -> PermissionState.PERMANENTLY_DENIED
            else -> PermissionState.DENIED
        }
    }

    fun locationState(
        activity: Activity?,
        askedBefore: Boolean
    ): PermissionState {
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val granted = coarseGranted || fineGranted

        return when {
            granted -> PermissionState.GRANTED
            activity != null &&
                    askedBefore &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) -> PermissionState.PERMANENTLY_DENIED
            else -> PermissionState.DENIED
        }
    }

    fun motionState(
        activity: Activity?,
        askedBefore: Boolean
    ): PermissionState {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val hasStepSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null ||
                    sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null

        if (!hasStepSensor) return PermissionState.UNSUPPORTED

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return PermissionState.NOT_REQUIRED
        }

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        return when {
            granted -> PermissionState.GRANTED
            activity != null &&
                    askedBefore &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) -> PermissionState.PERMANENTLY_DENIED
            else -> PermissionState.DENIED
        }
    }
}