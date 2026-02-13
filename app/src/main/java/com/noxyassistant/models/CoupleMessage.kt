package com.noxyassistant.models

data class CoupleMessage(
    val fromUser: String,
    val toUser: String,
    val text: String,
    val timestamp: Long
)
