package com.noxy.assistant.models

/**
 * Represents the state of a Noxy device for a given user.
 */
data class NoxyState(
    val userId: String,
    val deviceId: String,
    val mood: NoxyMood,
    val lastMessage: String?
)
