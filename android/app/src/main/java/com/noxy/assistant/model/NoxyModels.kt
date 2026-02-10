package com.noxy.assistant.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoxyWebRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("device_id") val deviceId: String,
    val message: String,
    val mood: String? = null
)

@Serializable
data class NoxyWebResponse(
    val reply: String,
    @SerialName("used_memory_summary") val usedMemorySummary: String? = null,
    @SerialName("personality_style") val personalityStyle: String? = null,
    val sources: List<String>? = null
)

enum class NoxyMood {
    HAPPY,
    CALM,
    SAD,
    EXCITED,
    NEUTRAL
}

/**
 * Contient l'état global de l'utilisateur pour construire les requêtes.
 */
data class NoxyState(
    val userId: String = "demo-user",
    val deviceId: String = "demo-device",
    val mood: NoxyMood = NoxyMood.HAPPY
)
