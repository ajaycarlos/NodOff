package com.example.nodoff.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nodoff.data.SettingsRepository
import com.example.nodoff.service.NodOffService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

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
}
