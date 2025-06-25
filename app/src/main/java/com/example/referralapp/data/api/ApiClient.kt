package com.example.referralapp.data.api

import android.content.Context
import com.example.referralapp.data.local.PrefManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Log

object AppClient {

    // 모든 API 요청을 위한 BASE_URL 설정
    private const val BASE_URL = "https://your.api.server/" // 실제 서버 주소로 교체

    // create() method를 통해 sharedPreferences를 받아 ApiServices 반환하는 방식
    fun create(context: Context): ApiService {
        Log.d("AppClient", "Initializing Appclient...")
        val sharedPreferences = context.getSharedPreferences("referral_app_prefs", Context.MODE_PRIVATE)
        Log.d("AppClient", "SharedPreferences acquired...")
        val prefManager = PrefManager(sharedPreferences)
        Log.d("AppClient", "PrefManager initialized... JWT Token: ${prefManager.getJwtToken()}")

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(prefManager)) // JWT 토큰 자동 삽입
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        Log.d("AppClient", "OkHttpClient built...")

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("AppClient", "Retrofit intance created with BASE_URL: $BASE_URL")

        return retrofit.create(ApiService::class.java)
            Log.d("AppClient", "ApiService created...")
    }
}
