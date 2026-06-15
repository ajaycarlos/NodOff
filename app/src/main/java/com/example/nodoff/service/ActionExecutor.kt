package com.example.nodoff.service

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.example.nodoff.data.SettingsRepository
import com.example.nodoff.data.db.EventEntity
import com.example.nodoff.data.db.NodOffDatabase
import kotlinx.coroutines.flow.first

class ActionExecutor(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val database = NodOffDatabase.getDatabase(context)
    private val eventDao = database.eventDao()

    /**
     * Pauses active background media by requesting transient audio focus
     * and simulating a KEYCODE_MEDIA_PAUSE key event.
     */
    fun pauseMedia() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return

        // 1. Request transient audio focus to signal players to pause
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener { /* No-op */ }
                .build()
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                { /* No-op */ },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
        }

        // 2. Dispatch a media pause key event as a robust fallback
        val time = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(time, time, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)
        val upEvent = KeyEvent(time, time, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    /**
     * Disables Bluetooth via BluetoothAdapter, ensuring BLUETOOTH_CONNECT permission
     * is handled for Android 12 (API 31) and higher.
     */
    fun disconnectBluetooth() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager ?: return
        val bluetoothAdapter = bluetoothManager.adapter ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                @Suppress("DEPRECATION")
                if (bluetoothAdapter.isEnabled) {
                    bluetoothAdapter.disable()
                }
            }
        } else {
            @Suppress("DEPRECATION")
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
            }
        }
    }

    /**
     * Locks the device's screen using DevicePolicyManager if NodOff has been
     * activated as a Device Administrator.
     */
    fun turnOffScreen() {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager ?: return
        val adminComponent = ComponentName(context, NodOffDeviceAdminReceiver::class.java)
        if (dpm.isAdminActive(adminComponent)) {
            dpm.lockNow()
        }
    }

    /**
     * Checks DataStore preferences and executes the enabled sleep actions.
     */
    suspend fun triggerSleepActions() {
        val timestamp = System.currentTimeMillis()
        if (settingsRepository.pauseMediaFlow.first()) {
            pauseMedia()
            eventDao.insertEvent(EventEntity(timestamp = timestamp, actionType = "PAUSE_MEDIA"))
        }
        if (settingsRepository.disconnectBluetoothFlow.first()) {
            disconnectBluetooth()
            eventDao.insertEvent(EventEntity(timestamp = timestamp, actionType = "DISCONNECT_BLUETOOTH"))
        }
        if (settingsRepository.turnOffScreenFlow.first()) {
            turnOffScreen()
            eventDao.insertEvent(EventEntity(timestamp = timestamp, actionType = "TURN_OFF_SCREEN"))
        }
    }
}
