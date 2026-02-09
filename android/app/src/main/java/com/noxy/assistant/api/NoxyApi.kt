package com.noxy.assistant.api

import com.noxy.assistant.model.NoxyWebRequest
import com.noxy.assistant.model.NoxyWebResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NoxyApi {
    @POST("noxy/web")
    suspend fun sendWeb(@Body request: NoxyWebRequest): NoxyWebResponse
}
