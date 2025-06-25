package com.example.referralapp.data.api

import com.example.referralapp.data.local.PrefManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class JwtInterceptor @Inject constructor(
    private val prefManager: PrefManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = prefManager.getJwtToken()

        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        if (originalRequest.method in listOf("POST", "PUT", "PATCH")) {
            requestBuilder.addHeader("Content-Type", "application/json")
        }

        return chain.proceed(requestBuilder.build())
    }
}
