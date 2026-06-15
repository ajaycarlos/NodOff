package com.example.nodoff.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nodoff.data.SettingsRepository
import com.example.nodoff.camera.EyeTrackingState
import com.example.nodoff.data.db.EventEntity
import com.example.nodoff.data.db.NodOffDatabase
import com.example.nodoff.service.NodOffService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    private val database = NodOffDatabase.getDatabase(application)
    private val eventDao = database.eventDao()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    val eyeTrackingState: StateFlow<EyeTrackingState> = NodOffService.trackingState

    val allEvents: StateFlow<List<EventEntity>> = eventDao.getAllEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleMonitoring() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, NodOffService::class.java)
        if (_isMonitoring.value) {
            context.stopService(intent)
            _isMonitoring.value = false
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _isMonitoring.value = true
        }
    }

    val pauseMedia: StateFlow<Boolean> = repository.pauseMediaFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val turnOffScreen: StateFlow<Boolean> = repository.turnOffScreenFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val disconnectBluetooth: StateFlow<Boolean> = repository.disconnectBluetoothFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val eyeCloseDelay: StateFlow<Float> = repository.eyeCloseDelayFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 15f
    )

    val pollingRate: StateFlow<Int> = repository.pollingRateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val automationApps: StateFlow<Set<String>> = repository.automationAppsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val themePreference: StateFlow<Int> = repository.themePreferenceFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val faceLostTimeoutSeconds: StateFlow<Int> = repository.faceLostTimeoutSecondsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 120
    )

    fun setPauseMedia(value: Boolean) {
        viewModelScope.launch {
            repository.setPauseMedia(value)
        }
    }

    fun setTurnOffScreen(value: Boolean) {
        viewModelScope.launch {
            repository.setTurnOffScreen(value)
        }
    }

    fun setDisconnectBluetooth(value: Boolean) {
        viewModelScope.launch {
            repository.setDisconnectBluetooth(value)
        }
    }

    fun setEyeCloseDelay(value: Float) {
        viewModelScope.launch {
            repository.setEyeCloseDelay(value)
        }
    }

    fun setPollingRate(value: Int) {
        viewModelScope.launch {
            repository.setPollingRate(value)
        }
    }

    fun addAutomationApp(packageName: String) {
        viewModelScope.launch {
            repository.addAutomationApp(packageName)
        }
    }

    fun removeAutomationApp(packageName: String) {
        viewModelScope.launch {
            repository.removeAutomationApp(packageName)
        }
    }

    fun setAutomationApps(packages: Set<String>) {
        viewModelScope.launch {
            repository.setAutomationApps(packages)
        }
    }

    fun getInstalledApps(): List<AppInfoItem> {
        val context = getApplication<Application>().applicationContext
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return pm.queryIntentActivities(intent, 0).mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            // Filter out apps that don't have a valid launch intent (filter out raw system services)
            if (pm.getLaunchIntentForPackage(packageName) != null) {
                val label = resolveInfo.loadLabel(pm).toString()
                val icon = try {
                    pm.getApplicationIcon(packageName)
                } catch (e: Exception) {
                    pm.defaultActivityIcon
                }
                AppInfoItem(label, packageName, icon)
            } else {
                null
            }
        }.distinctBy { it.packageName }.sortedBy { it.label }
    }

    fun setThemePreference(value: Int) {
        viewModelScope.launch {
            repository.setThemePreference(value)
        }
    }

    fun setFaceLostTimeoutSeconds(value: Int) {
        viewModelScope.launch {
            repository.setFaceLostTimeoutSeconds(value)
        }
    }
}

data class AppInfoItem(
    val label: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable?
)
