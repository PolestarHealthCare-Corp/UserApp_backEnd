package com.example.referralapp.data.repository

import android.util.Log
import com.example.referralapp.data.api.ApiService
import com.example.referralapp.data.model.HospitalInfo
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val apiService: ApiService
) {

    companion object {
        private const val TAG = "HospitalRepository"
    }

    /**
     * 공통 응답 처리 메서드
     */
    private fun <T> handleResponse(
        response: Response<T>,
        defaultErrorMessage: String
    ): Result<T> {
        return when {
            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Response is successful: $body")
                    Result.success(body)
                } else {
                    Log.w(TAG, "Response body is null")
                    Result.failure(Exception("Response body is null"))
                }
            }
            else -> {
                val code = response.code()
                val message = response.message()
                val errorBody = response.errorBody()?.string().orEmpty()

                val fullErrorMessage = buildString {
                    append(defaultErrorMessage)
                    append(" (HTTP $code)")
                    if (errorBody.isNotBlank()) append(" - $errorBody")
                }

                Log.e(TAG, """
                    - code: $code
                    - Message: $message
                    - Error Body: $errorBody
                    """.trimIndent())
                Result.failure(Exception(fullErrorMessage))
            }
        }
    }


    /**
     * 병원 정보 등록
     * @param hospitalInfo 병원 정보
     * @return 등록 결과
     */
    suspend fun registerHospital(hospitalInfo: HospitalInfo): Result<HospitalInfo> {
        return try {
            Log.d(TAG, "registerHospital() called: $hospitalInfo")
            val response = apiService.registerHospital(hospitalInfo)
            handleResponse(response, "병원 정보 등록 실패")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Error [${e.code()}]: ${e.message()}", e)
            Result.failure(Exception("네트워크 오류: ${e.message()}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network Error", e)
            // 사용자 메시지는 UI단에서 설정(ex: "네트워크 연결을 확인해주세요."
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Error: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    /**
     * 내가 등록한 병원 목록 조회
     * @return 병원 목록
     */
    suspend fun getMyHospitalList(dateFilter: String): Result<List<HospitalInfo>> {
        return try {
            Log.d(TAG, "getMyHospitalList() called with date: $dateFilter")
            val response = apiService.getMyHospitalList(dateFilter)
            handleResponse(response, "병원 목록 조회 실패")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Error [${e.code()}]: ${e.message()}", e)
            Result.failure(Exception("네트워크 오류: ${e.message()}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network Error: ${e.localizedMessage}", e)
            Result.failure(Exception("네트워크 연결 오류: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Error: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    /**
     * 소개 내역 조회
     * @return 소개 내역 목록
     */
    suspend fun getReferralHistory(): Result<List<HospitalInfo>> {
        return try {
            Log.d(TAG, "getReferralHistory() called")
            val response = apiService.getReferralHistory()
            handleResponse(response, "소개 내역 조회 실패")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Error [${e.code()}]: ${e.message()}", e)
            Result.failure(Exception("네트워크 오류: ${e.message()}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network Error: ${e.localizedMessage}", e)
            Result.failure(Exception("네트워크 연결 오류: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Error: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}