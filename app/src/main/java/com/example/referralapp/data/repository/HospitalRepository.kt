package com.example.referralapp.data.repository

import com.example.referralapp.data.api.ApiService
import com.example.referralapp.data.model.HospitalInfo
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 병원 정보 등록
     * @param hospitalInfo 병원 정보
     * return 등록 결과
     */
    suspend fun registerHospital(hospitalInfo: HospitalInfo): Result<HospitalInfo> {
        return try {
            val response = apiService.registerHospital(hospitalInfo)
            if(reponse.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            }   else {
                Result.failure(Exception("병원 정보 등록 실패"))
            }
        }   catch (e: HttpException) {
                Result.failure(Exception("네트워크 오류: ${e.message()}"))
        }   catch (e: Exception) {
                Result.failure(e)
        }
    }
    /**
     * 내가 등록한 병원 목록 조회
     * @return 병원 목록
     */
    suspend fun getMyHospitalList(): Result<List<HospitalInfo>> {
        return try {
            val response = apiService.getMyHospitalList()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("병원 목록 조회에 실패했습니다."))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("네트워크 오류: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 소개 내역 조회
     * @return 소개 내역 목록
     */
    suspend fun getReferralHistory(): Result<List<HospitalInfo>> {
        return try {
            val response = apiService.getReferralHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("소개 내역 조회에 실패했습니다."))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("네트워크 오류: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}