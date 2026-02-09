package com.noxy.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noxy.assistant.api.NoxyApi
import com.noxy.assistant.model.NoxyMood
import com.noxy.assistant.model.NoxyState
import com.noxy.assistant.model.NoxyWebRequest
import com.noxy.assistant.model.NoxyWebResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel minimal pour gérer les messages du chat et l'appel réseau /noxy/web.
 */
class NoxyChatViewModel(
    private val api: NoxyApi,
    private val initialState: NoxyState = NoxyState()
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<String>>(emptyList())
    val chatMessages: StateFlow<List<String>> = _chatMessages

    private val _debugInfo = MutableStateFlow<NoxyWebResponse?>(null)
    val debugInfo: StateFlow<NoxyWebResponse?> = _debugInfo

    var state: NoxyState = initialState
        private set

    fun updateMood(mood: NoxyMood) {
        state = state.copy(mood = mood)
    }

    fun sendMessage(inputText: String) {
        if (inputText.isBlank()) return
        val request = NoxyWebRequest(
            userId = state.userId,
            deviceId = state.deviceId,
            message = inputText,
            mood = state.mood.name
        )

        viewModelScope.launch {
            try {
                val response = api.sendWeb(request)
                appendMessage("Moi: $inputText")
                appendMessage("Noxy: ${response.reply}")
                _debugInfo.value = response
            } catch (ioe: IOException) {
                appendMessage("(Erreur réseau) Impossible de joindre Noxy : ${ioe.message}")
            } catch (he: HttpException) {
                appendMessage("(Erreur serveur) ${he.message()}")
            } catch (e: Exception) {
                appendMessage("(Erreur inattendue) ${e.message}")
            }
        }
    }

    private fun appendMessage(message: String) {
        _chatMessages.value = _chatMessages.value + message
    }
}
