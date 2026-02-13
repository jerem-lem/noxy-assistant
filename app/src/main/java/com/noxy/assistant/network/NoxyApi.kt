package com.noxy.assistant.network

import com.noxy.assistant.models.CoupleMessage
import com.noxy.assistant.models.GuardianState
import com.noxy.assistant.models.NoxyMessage
import com.noxy.assistant.models.NoxyMood
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit definition of the Noxy backend endpoints.
 */
interface NoxyApi {

    @POST("noxy/web")
    suspend fun sendNoxyMessage(@Body message: NoxyMessage): Response<Unit>

    @POST("guardian/register")
    suspend fun registerGuardian(@Body state: GuardianState): Response<Unit>

    @POST("guardian/push_command")
    suspend fun pushCommand(@Body request: PushCommandRequest): Response<Unit>

    @GET("guardian/commands")
    suspend fun getPendingCommands(): Response<List<String>>

    @POST("guardian/location")
    suspend fun sendLocation(@Body location: LocationUpdate): Response<Unit>

    @POST("couple/send")
    suspend fun sendCoupleMessage(@Body message: CoupleMessage): Response<Unit>

    @GET("couple/last_message")
    suspend fun getLastCoupleMessage(): Response<CoupleMessage?>

    @POST("user/set_mood")
    suspend fun setMood(@Body request: MoodUpdateRequest): Response<Unit>

    @GET("user/mood")
    suspend fun getMood(): Response<NoxyMood>

    data class PushCommandRequest(val command: String)

    data class LocationUpdate(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float? = null
    )

    data class MoodUpdateRequest(val mood: NoxyMood)
}
