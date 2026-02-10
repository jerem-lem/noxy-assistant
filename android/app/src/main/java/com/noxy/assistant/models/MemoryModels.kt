package com.noxy.assistant.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryItem(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: Double
)

@Serializable
data class PersonalityProfile(
    @SerialName("user_id") val userId: String,
    @SerialName("style") val style: String,
    @SerialName("tone") val tone: String,
    @SerialName("traits") val traits: List<String>
)

@Serializable
data class MemoryCreateRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("content") val content: String
)

@Serializable
data class PersonalityUpdateRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("style") val style: String? = null,
    @SerialName("tone") val tone: String? = null,
    @SerialName("traits") val traits: List<String>? = null
)

@Serializable
data class SummaryResponse(
    @SerialName("summary") val summary: String
)
