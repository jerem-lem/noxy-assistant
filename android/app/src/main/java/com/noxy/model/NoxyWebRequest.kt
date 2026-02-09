package com.noxy.model

data class NoxyWebRequest(
    val userId: String,
    val message: String,
    val mood: String? = null,
    val mode: String? = null,
)
