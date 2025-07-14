package com.example.referralapp.data.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.referralapp.data.local.PrefManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import androidx.security.crypto.MasterKey


object AppClient {

    // 모든 API 요청을 위한 BASE_URL 설정
    private const val BASE_URL = "https://your.api.server/" // 실제 서버 주소로 교체

    /**
     * create() method를 통해 SharedPreferences 기반으로 ApiService 반환
     * @param context 앱의 Context (SharedPreferences 접근용)
     * @return ApiService instance
     */
    fun create(context: Context): ApiService {
        Log.d("AppClient", "Initializing Appclient...")

        val standardPrefs = context.getSharedPreferences("referral_app_prefs", Context.MODE_PRIVATE)
        Log.d("AppClient", "SharedPreferences acquired...")

        // [1] 마스터 키 생성 또는 획득 (AES256_GCM 방식)
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPrefs: SharedPreferences
        try {

            // [2] 암호화 SharedPreferences 생성
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,                                 // context
                "referral_app_encrypted_prefs",          // fileName
                masterKey,                               // masterKey
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            Log.d("AppClient", "[EncryptedPrefs] 암호화된 SharedPreferences 초기화 성공")

        } catch (e: Exception) {
            // [3] 예외 처리 (실패 시 앱 실행에 중요한 문제로 간주하고 예외 발생)
            Log.e("AppClient", "[EncryptedPrefs] 초기화 실패 - 암호화 SharedPreferences 생성 중 오류 발생", e)

            // 필수 기능으로 간주 → 즉시 종료 또는 App 초기화 실패 알림
            throw RuntimeException("암호화 EncryptedSharedPreferences 초기화 실패 - 앱을 계속 실행할 수 없습니다.", e)

            /*
            // (선택적) Fallback 처리 예시 - 기본 SharedPreferences로 대체
            encryptedPrefs = context.getSharedPreferences("referral_app_fallback_prefs", Context.MODE_PRIVATE)
            Log.w("AppClient", "[EncryptedPrefs] Fallback 사용 - 암호화 불가, 일반 SharedPreferences 사용")
            */
        }

        val prefManager = PrefManager(standardPrefs, encryptedPrefs)
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

        Log.d("AppClient", "ApiService created...")
        return retrofit.create(ApiService::class.java)
    }
}
