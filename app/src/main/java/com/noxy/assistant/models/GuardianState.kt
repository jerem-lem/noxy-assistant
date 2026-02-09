package com.noxy.assistant.models

data class GuardianState(
    val safeMode: Boolean = false,
    val locked: Boolean = false,
    val lastLocationSentAt: Long? = null,
    val lastAlarmAt: Long? = null
)
