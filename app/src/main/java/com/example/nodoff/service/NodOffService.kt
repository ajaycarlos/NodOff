package com.example.nodoff.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.nodoff.R
import com.example.nodoff.camera.EyeStatus
import com.example.nodoff.camera.EyeTrackerManager
import com.example.nodoff.camera.EyeTrackingState
import com.example.nodoff.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NodOffService : LifecycleService() {

    companion object {
        private val _trackingState = MutableStateFlow(EyeTrackingState(EyeStatus.IDLE, 0L))
        val trackingState = _trackingState.asStateFlow()

        fun updateTrackingState(state: EyeTrackingState) {
            _trackingState.value = state
        }
    }

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var actionExecutor: ActionExecutor
    private lateinit var eyeTrackerManager: EyeTrackerManager

    private var isServiceStarted = false
    private var isActionTriggered = false

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        actionExecutor = ActionExecutor(applicationContext, settingsRepository)
        eyeTrackerManager = EyeTrackerManager(applicationContext, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!isServiceStarted) {
            isServiceStarted = true
            startForegroundNotification()
            eyeTrackerManager.start()
            observeEyeTracking()
        }

        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "nodoff_monitoring_channel"
        val channelName = "NodOff Monitoring"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("NodOff")
            .setContentText("Monitoring Active")
            .setSmallIcon(R.drawable.ic_nodoff_logo)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val notificationId = 101
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA)
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun observeEyeTracking() {
        lifecycleScope.launch {
            var thresholdMs = 15000L

            // Collect close delay from preferences dynamically
            launch {
                settingsRepository.eyeCloseDelayFlow.collect { delaySeconds ->
                    thresholdMs = (delaySeconds * 1000).toLong()
                }
            }

            // Collect and handle changes in eye status
            eyeTrackerManager.state.collect { state ->
                updateTrackingState(state)
                if (state.status == EyeStatus.EYES_CLOSED) {
                    if (state.closedDurationMs >= thresholdMs) {
                        if (!isActionTriggered) {
                            isActionTriggered = true
                            launch {
                                actionExecutor.triggerSleepActions()
                            }
                        }
                    }
                } else {
                    // Reset triggered flag when eyes are open or status is idle
                    isActionTriggered = false
                }
            }
        }
    }

    override fun onDestroy() {
        eyeTrackerManager.stop()
        updateTrackingState(EyeTrackingState(EyeStatus.IDLE, 0L))
        isServiceStarted = false
        super.onDestroy()
    }
}
