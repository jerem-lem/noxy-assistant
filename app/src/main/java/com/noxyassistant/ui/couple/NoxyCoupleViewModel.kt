package com.noxyassistant.ui.couple

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noxyassistant.models.CoupleMessage
import com.noxyassistant.models.NoxyMood
import com.noxyassistant.network.ApiResult
import com.noxyassistant.network.LastCoupleMessageResponse
import com.noxyassistant.network.MoodResponse
import com.noxyassistant.network.NoxyApi
import com.noxyassistant.network.SendCoupleMessageRequest
import com.noxyassistant.network.SetMoodRequest
import com.noxyassistant.widget.NoxyWidgetProvider
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoxyCoupleViewModel(
    application: Application,
    private val api: NoxyApi = InMemoryNoxyApi()
) : AndroidViewModel(application) {

    data class CoupleUiState(
        val myMood: NoxyMood = NoxyMood.HAPPY,
        val partnerMood: NoxyMood? = null,
        val lastMessage: CoupleMessage? = null,
        val inputText: String = "",
        val isSending: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _state = MutableStateFlow(CoupleUiState())
    val state: StateFlow<CoupleUiState> = _state.asStateFlow()

    fun onInputChange(new: String) {
        _state.update { it.copy(inputText = new, errorMessage = null) }
    }

    fun refreshMoodAndMessage() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val moodResult = api.getMood()
            val messageResult = api.getLastCoupleMessage()
            when (moodResult) {
                is ApiResult.Success -> applyMoodResponse(moodResult.data)
                is ApiResult.Error -> _state.update { it.copy(errorMessage = moodResult.throwable.message) }
            }
            when (messageResult) {
                is ApiResult.Success -> applyLastMessage(messageResult.data)
                is ApiResult.Error -> _state.update { it.copy(errorMessage = messageResult.throwable.message) }
            }
            _state.update { it.copy(isLoading = false) }
            updateWidget()
        }
    }

    fun sendCoupleMessage() {
        val messageText = state.value.inputText.trim()
        if (messageText.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isSending = true, errorMessage = null) }
            val request = SendCoupleMessageRequest(
                fromUser = DEMO_USER_ID,
                toUser = DEMO_PARTNER_ID,
                text = messageText
            )
            when (val result = api.sendCoupleMessage(request)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(inputText = "") }
                    refreshMoodAndMessage()
                }
                is ApiResult.Error -> _state.update { it.copy(errorMessage = result.throwable.message) }
            }
            _state.update { it.copy(isSending = false) }
        }
    }

    fun setMyMood(mood: NoxyMood) {
        viewModelScope.launch {
            _state.update { it.copy(myMood = mood, errorMessage = null) }
            when (val result = api.setMood(SetMoodRequest(DEMO_USER_ID, mood))) {
                is ApiResult.Success -> applyMoodResponse(result.data)
                is ApiResult.Error -> _state.update { it.copy(errorMessage = result.throwable.message) }
            }
            refreshMoodAndMessage()
        }
    }

    private fun applyMoodResponse(response: MoodResponse) {
        if (response.userId == DEMO_USER_ID) {
            _state.update { it.copy(myMood = response.mood) }
        } else {
            _state.update { it.copy(partnerMood = response.mood) }
        }
    }

    private fun applyLastMessage(response: LastCoupleMessageResponse) {
        _state.update {
            it.copy(
                lastMessage = response.message,
                partnerMood = response.partnerMood ?: it.partnerMood
            )
        }
    }

    private fun updateWidget() {
        val context = getApplication<Application>()
        val currentState = state.value
        NoxyWidgetProvider.updateAllWidgets(
            context = context,
            lastMessage = currentState.lastMessage?.text,
            currentMood = currentState.myMood
        )
    }

    companion object {
        private const val DEMO_USER_ID = "demo-user"
        private const val DEMO_PARTNER_ID = "demo-partner"
    }
}

/**
 * In-memory fallback API so the screen can function before real networking is wired.
 */
class InMemoryNoxyApi : NoxyApi {
    private var currentMood: NoxyMood = NoxyMood.HAPPY
    private var partnerMood: NoxyMood = NoxyMood.CALM
    private val messages = ArrayDeque<CoupleMessage>()
    private val clock = AtomicLong(System.currentTimeMillis())

    override suspend fun sendCoupleMessage(request: SendCoupleMessageRequest): ApiResult<Unit> {
        val message = CoupleMessage(
            fromUser = request.fromUser,
            toUser = request.toUser,
            text = request.text,
            timestamp = clock.incrementAndGet()
        )
        messages.addFirst(message)
        return ApiResult.Success(Unit)
    }

    override suspend fun getLastCoupleMessage(): ApiResult<LastCoupleMessageResponse> {
        val response = LastCoupleMessageResponse(
            message = messages.firstOrNull(),
            partnerMood = partnerMood
        )
        return ApiResult.Success(response)
    }

    override suspend fun setMood(request: SetMoodRequest): ApiResult<MoodResponse> {
        if (request.userId == "demo-partner") {
            partnerMood = request.mood
        } else {
            currentMood = request.mood
        }
        return ApiResult.Success(MoodResponse(request.userId, request.mood))
    }

    override suspend fun getMood(): ApiResult<MoodResponse> {
        return ApiResult.Success(MoodResponse(userId = "demo-user", mood = currentMood))
    }
}
