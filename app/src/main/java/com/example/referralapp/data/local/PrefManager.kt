package com.example.referralapp.data.local

import android.content.SharedPreferences
import com.example.referralapp.data.model.User
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }

    /**
     * 사용자 정보 저장
     */
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_USER, userJson)
            .apply()

        // JWT 토큰 별도 저장
        user.jwtToken?.let { token ->
            sharedPreferences.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply()
        }
    }

    /**
     * 사용자 정보 조회
     */
    fun getUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * JWT 토큰 조회
     */
    fun getJwtToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }

    /**
     * 사용자 정보 삭제 (로그아웃)
     */
    fun clearUser() {
        sharedPreferences.edit()
            .remove(KEY_USER)
            .remove(KEY_JWT_TOKEN)
            .apply()
    }

    /**
     * 첫 실행 여부 확인
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    /**
     * 첫 실행 완료 처리
     */
    fun setFirstLaunchCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
    }
}
