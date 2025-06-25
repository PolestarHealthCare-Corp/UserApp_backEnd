package com.example.referralapp.data.api

import android.content.Context
import com.example.referralapp.data.local.PrefManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AppClient {

    // 모든 API 요청을 위한 BASE_URL 설정
    private const val BASE_URL = "https://your.api.server/" // 실제 서버 주소로 교체

    // create() method를 통해 sharedPreferences를 받아 ApiServices 반환하는 방식
    fun create(context: Context): ApiService {
        val sharedPreferences = context.getSharedPreferences("referral_app_prefs", Context.MODE_PRIVATE)
        val prefManager = PrefManager(sharedPreferences)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(prefManager)) // JWT 토큰 자동 삽입
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
