package com.example.referralapp.data.repository

import android.util.Log
import com.example.referralapp.data.api.ApiService
import com.example.referralapp.data.local.PrefManager
import com.example.referralapp.data.model.User
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefManager: PrefManager
) {

    companion object {
        private const val TAG = "UserRepository"
    }

    /**
     * 공통 응답 처리 메서드
     */
    private fun <T> handleResponse(
        response: Response<T>,
        defaultErrorMessage: String
    ): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Log.d(TAG, "API 성공 응답: ${response.code()}")
                Result.success(body)
            } else {
                Log.w(TAG, "응답은 성공했지만 body가 null입니다.")
                Result.failure(Exception("응답 데이터가 없습니다"))
            }
        } else {
            val code = response.code()
            val errorBody = try {
                response.errorBody()?.string().orEmpty()
            } catch (e: Exception) {
                "에러 메시지 파싱 실패"
            }

            val fullErrorMessage = buildString {
                append(defaultErrorMessage)
                append(" (HTTP $code)")
                if (errorBody.isNotBlank()) append(" - $errorBody")
            }

            Log.e(TAG, "API 실패 응답: $fullErrorMessage")
            Result.failure(Exception(fullErrorMessage))
        }
    }

    /**
     * 로그인 처리
     */
    suspend fun login(user: User): Result<User> {
        return try {
            if (!user.isValid()) {
                return Result.failure(Exception("이름과 연락처를 모두 입력해주세요"))
            }

            Log.d(TAG, "로그인 요청: 이름=${user.memberName}, 연락처=${user.memberPhone}")

            val response = apiService.login(user)
            val result = handleResponse(response, "로그인 실패했습니다")

            result.onSuccess { loggedInUser ->
                try {
                    prefManager.saveUser(loggedInUser)
                    Log.d(TAG, "사용자 정보 로컬 저장 완료: 이름=${loggedInUser.memberName}, 연락처=${loggedInUser.memberPhone}")
                } catch (e: Exception) {
                    Log.e(TAG, "사용자 정보 저장 실패: ${e.message}", e)
                }
            }

            result

        } catch (e: HttpException) {
            val errorMsg = "서버 오류 발생 (${e.code()})"
            Log.e(TAG, "$errorMsg: ${e.message()}", e)
            Result.failure(Exception(errorMsg))

        } catch (e: IOException) {
            val errorMsg = "네트워크 연결을 확인해주세요"
            Log.e(TAG, "$errorMsg: ${e.message}", e)
            Result.failure(Exception(errorMsg))

        } catch (e: Exception) {
            val errorMsg = "예상치 못한 오류 발생"
            Log.e(TAG, "$errorMsg: ${e.message}", e)
            Result.failure(Exception(errorMsg))
        }
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    fun getCurrentUser(): User? {
        return try {
            val user = prefManager.getUser()
            if (user != null) {
                Log.d(TAG, "저장된 사용자 정보 조회 성공: 이름=${user.memberName}, 연락처=${user.memberPhone}")
            } else {
                Log.d(TAG, "저장된 사용자 정보 없음")
            }
            user
        } catch (e: Exception) {
            Log.e(TAG, "사용자 정보 조회 실패: ${e.message}", e)
            null
        }
    }

    /**
     * 로그아웃 처리
     */
    fun logout(): Boolean {
        return try {
            Log.d(TAG, "로그아웃 요청 시작")

            val currentUser = prefManager.getUser()
            if (currentUser != null) {
                Log.d(TAG, "로그아웃 사용자: 이름=${currentUser.memberName}, 연락처=${currentUser.memberPhone}")
            }

            prefManager.clearUser()
            Log.d(TAG, "로컬 사용자 정보 삭제 완료")
            true

        } catch (e: Exception) {
            Log.e(TAG, "로그아웃 처리 중 오류: ${e.message}", e)
            false
        }
    }

    /**
     * 로그인 여부 확인
     */
    fun isLoggedIn(): Boolean {
        return try {
            val user = prefManager.getUser()
            val isLoggedIn = user != null

            Log.d(TAG, "로그인 상태: $isLoggedIn")
            if (isLoggedIn) {
                Log.d(TAG, "로그인된 사용자: 이름=${user!!.memberName}, 연락처=${user.memberPhone}")
            }

            isLoggedIn
        } catch (e: Exception) {
            Log.e(TAG, "로그인 상태 확인 오류: ${e.message}", e)
            false
        }
    }
}
