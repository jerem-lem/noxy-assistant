package com.noxy.assistant.models

/**
 * Represents the guardian (safety) state for a paired device.
 */
data class GuardianState(
    val safeMode: Boolean,
    val locked: Boolean,
    val lastLocationSentAt: Long?
)
