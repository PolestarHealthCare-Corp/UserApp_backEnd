package com.example.referralapp.data.local

import android.content.SharedPreferences
import android.util.Log
import com.example.referralapp.data.model.User
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

/**
 * 사용자 정보 및 JWT Token을 SharedPreferences에 저장/관리하는 클래스
 * - 일반 정보(이름/연락처/첫 실행 여부)는 standardPrefs에 저장
 * - 민감 정보(JWT Token)는 encryptedPrefs에 저장
 */
@Singleton
class PrefManager @Inject constructor(
    @Named("standard_prefs") private val standardPrefs: SharedPreferences,
    @Named("encrypted_prefs") private val encryptedPrefs: SharedPreferences
) {
    private val gson = Gson()

    companion object {
        private const val TAG = "PrefManager"
        private const val KEY_USER = "user"
        private const val KEY_JWT_TOKEN = "jwt_token"
        // private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }

    /** User 객체 저장 + JWT Token 별도 저장 */
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        standardPrefs.edit()
            .putString(KEY_USER, userJson)
            .apply()
        Log.d(TAG, "User 저장됨 → $userJson")

        // JWT 토큰은 암호화 prefs에 저장
        user.jwtToken?.let { token ->
            encryptedPrefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply()
            Log.d(TAG, "JWT Token 저장됨 → $token")
        }
    }

    /** 저장된 User 객체 반환 */
    fun getUser(): User? {
        val userJson = standardPrefs.getString(KEY_USER, null)
        return if (!userJson.isNullOrEmpty()) {
            try {
                val user = gson.fromJson(userJson, User::class.java)
                Log.d(TAG, "User 불러옴 → $user")
                user
            } catch (e: Exception) {
                Log.e(TAG, "User JSON 파싱 실패", e)
                null
            }
        } else {
            Log.w(TAG, "저장된 User 없음")
            null
        }
    }

    /** 저장된 JWT Token 반환 */
    fun getJwtToken(): String? {
        val token = encryptedPrefs.getString(KEY_JWT_TOKEN, null)
        if (token != null) {
            Log.d(TAG, "JWT Token 불러옴 → $token")
        } else {
            Log.w(TAG, "JWT Token 없음")
        }
        return token
    }

    /**
     * 저장 정보 삭제
     * @param isFullReset true일 경우 앱 최초 실행 여부도 초기화함
     */
    fun clearUser(isFullReset: Boolean = false) {
        standardPrefs.edit().apply {
            remove(KEY_USER)
            if (isFullReset) {
                // remove(KEY_IS_FIRST_LAUNCH)
                apply()
            }
        }
        encryptedPrefs.edit().remove(KEY_JWT_TOKEN).apply()

        Log.d(TAG, "사용자 정보 및 JWT Token 삭제됨 (isFullReset=$isFullReset)")
    }
    /**
     *      온보딩(앱 첫 실행여부에 따른 추가 동작 있을 때 구현 예정

    /** 앱 첫 실행 여부 확인 */
    fun isFirstLaunch(): Boolean {
        val result = standardPrefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        Log.d(TAG, "isFirstLaunch: $result")
        return result
    }

    /** 앱이 더 이상 첫 실행이 아님을 표시 */
    fun setFirstLaunchCompleted() {
        standardPrefs.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
        Log.d(TAG, "첫 실행 플래그 비활성화됨")
    }
    */
}
