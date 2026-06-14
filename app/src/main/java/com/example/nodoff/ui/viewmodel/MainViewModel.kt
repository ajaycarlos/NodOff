package com.example.nodoff.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nodoff.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

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
