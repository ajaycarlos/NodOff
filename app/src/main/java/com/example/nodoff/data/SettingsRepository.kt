package com.example.nodoff.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Using Android's standard local sandbox Preferences DataStore, which is private to the application
// and secured by the OS signature permission model to prevent unauthorized data scraping.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nodoff_settings")

class SettingsRepository(private val context: Context) {

    private val dataStore = context.dataStore

    object PreferencesKeys {
        val PAUSE_MEDIA = booleanPreferencesKey("pause_media")
        val TURN_OFF_SCREEN = booleanPreferencesKey("turn_off_screen")
        val DISCONNECT_BLUETOOTH = booleanPreferencesKey("disconnect_bluetooth")
        val EYE_CLOSE_DELAY = floatPreferencesKey("eye_close_delay")
        val POLLING_RATE = intPreferencesKey("polling_rate")
        val AUTOMATION_APPS = stringSetPreferencesKey("automation_apps")
    }

    val pauseMediaFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAUSE_MEDIA] ?: true
    }

    val turnOffScreenFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TURN_OFF_SCREEN] ?: true
    }

    val disconnectBluetoothFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DISCONNECT_BLUETOOTH] ?: true
    }

    val eyeCloseDelayFlow: Flow<Float> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EYE_CLOSE_DELAY] ?: 15f
    }

    val pollingRateFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.POLLING_RATE] ?: 0
    }

    val automationAppsFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTOMATION_APPS] ?: emptySet()
    }

    suspend fun setPauseMedia(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAUSE_MEDIA] = value
        }
    }

    suspend fun setTurnOffScreen(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TURN_OFF_SCREEN] = value
        }
    }

    suspend fun setDisconnectBluetooth(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DISCONNECT_BLUETOOTH] = value
        }
    }

    suspend fun setEyeCloseDelay(value: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EYE_CLOSE_DELAY] = value
        }
    }

    suspend fun setPollingRate(value: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.POLLING_RATE] = value
        }
    }

    suspend fun addAutomationApp(packageName: String) {
        dataStore.edit { preferences ->
            val currentApps = preferences[PreferencesKeys.AUTOMATION_APPS] ?: emptySet()
            preferences[PreferencesKeys.AUTOMATION_APPS] = currentApps + packageName
        }
    }

    suspend fun removeAutomationApp(packageName: String) {
        dataStore.edit { preferences ->
            val currentApps = preferences[PreferencesKeys.AUTOMATION_APPS] ?: emptySet()
            preferences[PreferencesKeys.AUTOMATION_APPS] = currentApps - packageName
        }
    }
}
