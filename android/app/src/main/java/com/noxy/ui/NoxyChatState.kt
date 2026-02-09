package com.noxy.ui

import com.noxy.model.NoxyWebResponse

data class NoxyChatState(
    val isLoading: Boolean = false,
    val messages: List<String> = emptyList(),
    val errorMessage: String? = null,
    val lastResponse: NoxyWebResponse? = null,
)
