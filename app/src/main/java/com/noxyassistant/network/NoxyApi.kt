package com.noxyassistant.network

import com.noxyassistant.models.CoupleMessage
import com.noxyassistant.models.NoxyMood
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NoxyApi {
    @POST("/couple/send")
    suspend fun sendCoupleMessage(@Body request: SendCoupleMessageRequest): ApiResult<Unit>

    @GET("/couple/last_message")
    suspend fun getLastCoupleMessage(): ApiResult<LastCoupleMessageResponse>

    @POST("/user/set_mood")
    suspend fun setMood(@Body request: SetMoodRequest): ApiResult<MoodResponse>

    @GET("/user/mood")
    suspend fun getMood(): ApiResult<MoodResponse>
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val throwable: Throwable) : ApiResult<Nothing>()
}

data class SendCoupleMessageRequest(
    val fromUser: String,
    val toUser: String,
    val text: String
)

data class LastCoupleMessageResponse(
    val message: CoupleMessage?,
    val partnerMood: NoxyMood?
)

data class SetMoodRequest(
    val userId: String,
    val mood: NoxyMood
)

data class MoodResponse(
    val userId: String,
    val mood: NoxyMood
)
