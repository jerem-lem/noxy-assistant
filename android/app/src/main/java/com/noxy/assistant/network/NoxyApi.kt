package com.noxy.assistant.network

import com.noxy.assistant.models.MemoryCreateRequest
import com.noxy.assistant.models.MemoryItem
import com.noxy.assistant.models.PersonalityProfile
import com.noxy.assistant.models.PersonalityUpdateRequest
import com.noxy.assistant.models.SummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NoxyApi {
    @POST("/memory/add")
    suspend fun addMemory(@Body req: MemoryCreateRequest): MemoryItem

    @GET("/memory/list")
    suspend fun listMemory(@Query("user_id") userId: String): List<MemoryItem>

    @GET("/memory/summary")
    suspend fun memorySummary(@Query("user_id") userId: String): SummaryResponse

    @GET("/personality/get")
    suspend fun getPersonality(@Query("user_id") userId: String): PersonalityProfile

    @POST("/personality/update")
    suspend fun updatePersonality(@Body req: PersonalityUpdateRequest): PersonalityProfile
}
