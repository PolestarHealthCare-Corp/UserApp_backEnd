package com.example.referralapp.data.api

import android.util.Log
import com.example.referralapp.data.local.PrefManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import java.io.IOException


class JwtInterceptor @Inject constructor(
    private val prefManager: PrefManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = prefManager.getJwtToken()

        Log.d("JwtInterceptor", "started...")
        Log.d("JwtInterceptor", "URL: ${originalRequest.url}")
        Log.d("JwtInterceptor", "Method: ${originalRequest.method}")
        Log.d("JwtInterceptor", "Headers(before): ${originalRequest.headers}")
        Log.d("JwtInterceptor", "JWT Token: ${token ?: "Null"}")

        val requestBuilder = originalRequest.newBuilder()

        // JWT 추가 여부 Logging
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("JwtInterceptor", "Authorization Header Added...")
        } else {
            Log.w("JwtInterceptor", "JWT Token is null or blank...")
        }

        // Content-Type Header 추가 여부 로깅
        if (originalRequest.method in listOf("POST", "PUT", "PATCH")) {
            requestBuilder.addHeader("Content-Type", "application/json")
            Log.d("JwtInterceptor", "Content-Type application/json(Header) Added...")
        }

        val newRequest = requestBuilder.build()
        Log.d("JwtInterceptor", "Headers(after): ${newRequest.headers}")
        Log.d("JwtInterceptor", "Sending Request to the Server...")

        // try-catch로 예외 처리 및 요청/응답 로그
        return try {
            // D/JwtInterceptor : "Send Request: POST https://api.server/register"
            Log.d("JwtInterceptor", "Send Request: ${newRequest.method} ${newRequest.url}")
            val response = chain.proceed(newRequest)
            // D/JwtInterceptor : "Response(Status) Code: 200 OK"
            Log.d("JwtInterceptor", "Response(Status) Code: ${response.code} ${response.message}")

            response
            /*
            *   catch (e: IOException)로 Error catch --> Log.e 출력 후 Exception 재전파(throw e)
            *   --> E/JwtInterceptor : "Network Error: timeout"
            */
        }   catch (e: IOException) {
                Log.e("JwtInterceptor", "Network Error: ${e.localizedMessage}", e)
            throw e     // 예외 재전파
            }
        }
    }