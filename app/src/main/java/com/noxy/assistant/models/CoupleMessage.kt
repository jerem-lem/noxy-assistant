package com.noxy.assistant.models

/**
 * Message exchanged between members of a couple.
 */
data class CoupleMessage(
    val fromUser: String,
    val toUser: String,
    val text: String,
    val timestamp: Long
)
