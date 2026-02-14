package com.noxy.assistant.network

import com.noxy.assistant.models.GuardianCommand
import com.noxy.assistant.models.GuardianCommandType
import kotlinx.coroutines.delay

/**
 * Placeholder API client to be replaced with a real implementation.
 */
object NoxyApi {

    suspend fun getGuardianCommands(deviceId: String): List<GuardianCommand> {
        // Simulate network latency and return an empty list by default.
        delay(300)
        return emptyList()
    }

    suspend fun sendGuardianLocation(
        userId: String,
        deviceId: String,
        latitude: Double,
        longitude: Double
    ): Long {
        delay(200)
        // Pretend the backend returns the timestamp the location was accepted.
        return System.currentTimeMillis()
    }

    suspend fun postGuardianEvent(
        userId: String,
        deviceId: String,
        eventType: GuardianCommandType
    ): Long {
        delay(150)
        return System.currentTimeMillis()
    }
}
