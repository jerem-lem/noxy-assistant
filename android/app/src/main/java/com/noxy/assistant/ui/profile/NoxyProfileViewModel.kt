package com.noxy.assistant.ui.profile

import androidx.lifecycle.ViewModel
import com.noxy.assistant.models.MemoryCreateRequest
import com.noxy.assistant.models.MemoryItem
import com.noxy.assistant.models.PersonalityProfile
import com.noxy.assistant.models.PersonalityUpdateRequest
import com.noxy.assistant.network.NoxyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


data class ProfileUiState(
    val userId: String = "demo-user",
    val personality: PersonalityProfile? = null,
    val memoryItems: List<MemoryItem> = emptyList(),
    val newMemoryText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NoxyProfileViewModel(
    private val api: NoxyApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    suspend fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val userId = _uiState.value.userId
            val personality = api.getPersonality(userId)
            val memoryItems = api.listMemory(userId)
            _uiState.update { state ->
                state.copy(
                    personality = personality,
                    memoryItems = memoryItems,
                    isLoading = false
                )
            }
        } catch (t: Throwable) {
            _uiState.update { it.copy(isLoading = false, errorMessage = t.message) }
        }
    }

    fun onNewMemoryChange(new: String) {
        _uiState.update { it.copy(newMemoryText = new) }
    }

    suspend fun addMemory() {
        val state = _uiState.value
        if (state.newMemoryText.isBlank()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val userId = state.userId
            api.addMemory(
                MemoryCreateRequest(userId = userId, content = state.newMemoryText)
            )
            val updatedList = api.listMemory(userId)
            _uiState.update {
                it.copy(
                    memoryItems = updatedList,
                    newMemoryText = "",
                    isLoading = false
                )
            }
        } catch (t: Throwable) {
            _uiState.update { it.copy(isLoading = false, errorMessage = t.message) }
        }
    }

    suspend fun updatePersonality(style: String?, tone: String?, traits: List<String>?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val userId = _uiState.value.userId
            val updatedProfile = api.updatePersonality(
                PersonalityUpdateRequest(
                    userId = userId,
                    style = style,
                    tone = tone,
                    traits = traits
                )
            )
            _uiState.update {
                it.copy(personality = updatedProfile, isLoading = false)
            }
        } catch (t: Throwable) {
            _uiState.update { it.copy(isLoading = false, errorMessage = t.message) }
        }
    }
}
