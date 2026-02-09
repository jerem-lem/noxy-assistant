package com.noxy.api

import com.noxy.model.NoxyWebRequest
import com.noxy.model.NoxyWebResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NoxyApi {
    @POST("/noxy/web")
    suspend fun sendMessage(@Body request: NoxyWebRequest): NoxyWebResponse
}
