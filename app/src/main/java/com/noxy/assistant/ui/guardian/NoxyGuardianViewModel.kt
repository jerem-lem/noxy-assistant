package com.noxy.assistant.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noxy.assistant.models.GuardianCommandType
import com.noxy.assistant.models.GuardianState
import com.noxy.assistant.network.NoxyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoxyGuardianViewModel : ViewModel() {

    private val _state = MutableStateFlow(GuardianState())
    val state: StateFlow<GuardianState> = _state.asStateFlow()

    private val userId: String = "demo-user"
    private val deviceId: String = "demo-device"

    fun activateSafeModeLocal() {
        _state.value = _state.value.copy(safeMode = true, locked = true)
        recordBackendEvent(GuardianCommandType.SAFE_MODE)
    }

    fun requestDisableSafeMode(pin: String) {
        if (pin == "1234") {
            _state.value = _state.value.copy(safeMode = false, locked = false)
        }
    }

    fun sendLocationNow() {
        viewModelScope.launch {
            val timestamp = NoxyApi.sendGuardianLocation(
                userId = userId,
                deviceId = deviceId,
                latitude = 0.0,
                longitude = 0.0
            )
            _state.value = _state.value.copy(lastLocationSentAt = timestamp)
        }
    }

    fun triggerAlarmTest() {
        val timestamp = System.currentTimeMillis()
        _state.value = _state.value.copy(lastAlarmAt = timestamp)
        recordBackendEvent(GuardianCommandType.ALARM)
    }

    fun fetchAndApplyCommands() {
        viewModelScope.launch {
            val commands = NoxyApi.getGuardianCommands(deviceId)
            commands.forEach { command ->
                when (command.type) {
                    GuardianCommandType.SAFE_MODE -> activateSafeModeLocal()
                    GuardianCommandType.SEND_LOCATION -> sendLocationNow()
                    GuardianCommandType.ALARM -> triggerAlarmTest()
                    GuardianCommandType.LOCK_NOXY -> _state.value = _state.value.copy(locked = true)
                }
            }
        }
    }

    private fun recordBackendEvent(type: GuardianCommandType) {
        viewModelScope.launch {
            NoxyApi.postGuardianEvent(userId, deviceId, type)
        }
    }
}
