package com.example.referralapp.data.repository

import com.example.referralapp.data.api.ApiService
import com.example.referralapp.data.local.PrefManager
import com.example.referralapp.data.model.User
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefManager: PrefManager
) {

    /**
     * 로그인 처리
     * @param user 사용자 정보
     * @return 성공 여부
     */
    suspend fun loginOrRegister(user: User): Boolean {
        return try {
            val response = apiService.loginOrRegister(user)
            if (response.isSuccessful && response.body() != null) {
                val loggedInUser = response.body()!!
                // 사옹자 정보 로컬 저장
                prefManager.saveUser(loggedInUser)
                true
            } else {
                false
            }
        }   catch (e: Exception) {
            false
        }   catch (e: Exception) {
            false
        }
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    fun getCurrentUser(): User? {
        return prefManager.getUser()
    }

    /**
     * 로그아웃 처리
     */
    fun logout() {
        prefManager.clearUser()
    }

    /**
     * 로그인 상태 확인
     */
    fun isLoggedIn(): Boolean {
        return prefManager.getUser() != null
    }
}