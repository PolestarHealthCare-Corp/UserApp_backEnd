package com.example.referralapp.data.local

import android.util.Log
import android.content.SharedPreferences
import com.example.referralapp.data.model.User
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named
import android.content.ContentValues.TAG


/**
*   SharedPreferences를 통해 사용자 정보, JWT Token 등을 저장, 조회, 관리하는 클래스
*   SharedPreferences는 App 내에서 사용자 설정을 저장하고 불러오는 용도.
*   @Inject를 통해 의존성 주입 받음 ( 표준 + 암호화된 SharedPreferences 모두)
*/

/** 주요 Method 기능 설명
 *   - saveUserInfo(name, phone)    : 이름/연락처 저장. 앱 컷다 켜도 유지됨
 *   - loadUserInfo()               : 저장된 이름/연락처 가져오기
 *   - containUserInfo()            : 이름+연락처가 저장되어 있으면 true
 *   - saveUser(user)               : User 객체 전체 저장(이름, 연락처, 토큰  포함)
 *   - getUser()                    : 저장된 User 객체를 가져옴
 *   - getJwtToken()                : 저장된 JWT 토큰만 따로 가져옴
 *   - clearUser(isFullReset)       : 저장된 모든 사용자 정보 제거. true면 앱 최초 실행 플래그까지 지움.
 *   - isFirstLaunch()              : 앱이 처음 실행되는지 확인(최초 실행이면 true)
 *   - setFirstLaunchCompleted()    : "첫 실행 아님"으로 표시함
 */
@Singleton
class PrefManager @Inject constructor(
    @Named("standard_prefs") private val standardPrefs: SharedPreferences,
    @Named("encrypted_prefs") private val encryptedPrefs: SharedPreferences
) {
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
    }

    /**
    *   이름/연락처 별도 저장(간편 로그인)
     */
    fun saveUserInfo(name: String, phone: String) {
        standardPrefs.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_PHONE, phone)
            apply()
        }
        Log.d(TAG, "UserInfo saved: name=$name, phone=$phone")
    }

    /**
    *   이름/연락처 불러오기 (App 재실행 시 자동 로그인용)
     */
    fun loadUserInfo(): Pair<String?, String?> {
        val name = standardPrefs.getString(KEY_NAME, null)
        val phone = standardPrefs.getString(KEY_PHONE, null)
        Log.d(TAG, "UserInfo loaded: name=$name, phone=$phone")
        return Pair(name, phone)
    }

    /** 이름/연락처가 저장되어 있는지 여부 확인 */
    fun ContainUserInfo(): Boolean {
        val (name, phone) = loadUserInfo()
        return !name.isNullOrEmpty() && !phone.isNullOrEmpty()
    }

    /**
     *  사용자 정보 저장
     *  1) edit() method로 Editor 객체 획득
     *  2) putString() method --> JSON 문자열로 변환한 사용자 정보 저장
     *  3) apply(), commit()으로 저장
     */
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        standardPrefs.edit()
            .putString(KEY_USER, userJson)
            .apply()

        Log.d(TAG, "User saved: $userJson")

        // JWT 토큰 별도 저장
        user.jwtToken?.let { token ->
            encryptedPrefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply()
            Log.d(TAG, "JWT Token saved: $token")
        }
    }

    /**
     * 사용자 전체 정보 불러오기
     */
    fun getUser(): User? {
        val userJson = standardPrefs.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                val user = gson.fromJson(userJson, User::class.java)
                Log.d(TAG, "User loaded: $user")
                user
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user Parsing", e)
                null
            }
        } else {
            Log.w(TAG, "User not found")
            null
        }
    }

    /**
     * JWT 토큰 조회
     */
    fun getJwtToken(): String? {
        val token = encryptedPrefs.getString(KEY_JWT_TOKEN, null)
        if (token != null) {
            Log.d(TAG, "JWT Token loaded: $token")
        }   else {
                Log.w(TAG, "JWT Token not found")
        }
        return token
    }

    /**
     * 사용자 정보 삭제 (로그아웃)
     */
    fun clearUser(isFullReset: Boolean = false) {
        standardPrefs.edit().apply {
            remove(KEY_USER)
            remove(KEY_NAME)
            remove(KEY_PHONE)
            if (isFullReset) {
                remove(KEY_IS_FIRST_LAUNCH)
            }
            apply()
            Log.d(TAG, "User and JWT Token cleared")
        }

        // 민감한 정보(JWT Token) 저장용
        encryptedPrefs.edit().apply {
            remove(KEY_JWT_TOKEN)
            apply()
        }
    }

    /**
     * 첫 실행 여부 확인
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = standardPrefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        Log.d(TAG, "isFirstLaunch: $isFirst")
        return isFirst
    }

    /**
     * 첫 실행 플래그를 false로 변경
     */
    fun setFirstLaunchCompleted() {
        standardPrefs.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
        Log.d(TAG, "First launch completed")
    }
}
