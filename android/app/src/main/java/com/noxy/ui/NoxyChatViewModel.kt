package com.noxy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noxy.api.NoxyApi
import com.noxy.model.NoxyWebRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoxyChatViewModel(
    private val api: NoxyApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(NoxyChatState())
    val state: StateFlow<NoxyChatState> = _state

    var defaultMode: String = "CHAT"
        private set

    fun setMode(mode: String) {
        defaultMode = mode
    }

    fun sendMessage(userId: String, message: String, mood: String? = null, mode: String? = null) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatcher) {
            try {
                val resolvedMode = mode ?: defaultMode
                val request = NoxyWebRequest(
                    userId = userId,
                    message = message,
                    mood = mood,
                    mode = resolvedMode,
                )
                val response = api.sendMessage(request)
                val updatedMessages = _state.value.messages + listOf(message, response.reply)
                _state.value = _state.value.copy(
                    isLoading = false,
                    messages = updatedMessages,
                    lastResponse = response,
                )
            } catch (ex: Exception) {
                val fallbackMessage = "Noxy a un problème pour contacter le serveur. Réessaie plus tard."
                val updatedMessages = _state.value.messages + fallbackMessage
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: fallbackMessage,
                    messages = updatedMessages,
                )
            }
        }
    }
}
