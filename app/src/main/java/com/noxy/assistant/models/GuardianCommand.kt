package com.noxy.assistant.models

enum class GuardianCommandType {
    SAFE_MODE,
    SEND_LOCATION,
    ALARM,
    LOCK_NOXY
}

data class GuardianCommand(
    val type: GuardianCommandType,
    val createdAt: Long
)
