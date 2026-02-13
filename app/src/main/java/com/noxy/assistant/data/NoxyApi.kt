package com.noxy.assistant.data

import kotlinx.coroutines.delay

/**
 * Basic mock of the backend NoxyApi client. Replace with the real implementation
 * when the backend module is wired in.
 */
class NoxyApi {
    /**
     * Simulate a backend call to send a chat message and receive a reply.
     */
    suspend fun sendMessage(
        userId: String,
        deviceId: String,
        message: String
    ): String {
        // Simulate network latency
        delay(500)
        return "Echo: $message"
    }
}
