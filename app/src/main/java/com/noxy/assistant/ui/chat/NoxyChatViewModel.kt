package com.noxy.assistant.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noxy.assistant.data.NoxyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel handling chat interactions for the Noxy chat screen.
 */
class NoxyChatViewModel(
    private val api: NoxyApi = NoxyApi()
) : ViewModel() {

    data class ChatUiState(
        val messages: List<ChatMessage> = emptyList(),
        val inputText: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    data class ChatMessage(
        val text: String,
        val isUser: Boolean
    )

    private val userId: String = initializeUserId()
    private val deviceId: String = initializeDeviceId()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun onInputChange(new: String) {
        _uiState.update { current ->
            current.copy(inputText = new)
        }
    }

    fun sendMessage() {
        val trimmed = _uiState.value.inputText.trim()
        if (trimmed.isEmpty()) return

        _uiState.update { current ->
            current.copy(
                messages = current.messages + ChatMessage(text = trimmed, isUser = true),
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                api.sendMessage(userId = userId, deviceId = deviceId, message = trimmed)
            }.onSuccess { response ->
                _uiState.update { current ->
                    current.copy(
                        messages = current.messages + ChatMessage(text = response, isUser = false),
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Une erreur est survenue"
                    )
                }
            }
        }
    }

    private fun initializeUserId(): String = "demo-user"

    private fun initializeDeviceId(): String = "demo-device"
}
