package com.example.referralapp.data.api

import com.example.referralapp.data.model.User
import com.example.referralapp.data.model.HospitalInfo
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiService {

    /**
     * 사용자 로그인
     * @param user 사용자 정보 (이름, 전화번호)
     * @return 로그인 결과와 사용자 정보
     */
    @POST("/api/user/login")
    suspend fun login(@Body user: User): Response<User>

    /**
     * 병원 정보 등록
     * @param hospitalInfo 병원 정보
     * @return 등록 결과
     */
    @POST("/api/hospital/register")
    suspend fun registerHospital(@Body hospitalInfo: HospitalInfo): Response<HospitalInfo>

    /**
     * 내가 등록한 병원 목록 조회
     * @return 등록된 병원 목록
     */
    @GET("/api/hospital/my")
    suspend fun getMyHospitalList(@Query("date_Filter") date: String): Response<List<HospitalInfo>>

    /**
     * 소개 내역 조회
     * @return 소개 내역 목록
     */
    @GET("/api/referral/history")
    suspend fun getReferralHistory(): Response<List<HospitalInfo>>
}