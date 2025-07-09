package com.example.referralapp.data.model

import android.util.Log
import com.google.gson.annotations.SerializedName


data class HospitalInfo(

    // AUTO_INCREMENT 이므로 null 허용
    @SerializedName("hospital_info_id")
    val hospitalInfoId: Long? = null,

    // FK Key. 사용자(user)의 Phone과 연결
    @SerializedName("member_phone")
    val memberPhone: String,

    // 병원명
    @SerializedName("hospital_info_name")
    val hospitalInfoName: String,

    // 병원 주소(행정안전부 API)
    @SerializedName("hospital_info_address")
    val hospitalInfoAddress: String,

    // 병원 담당자 이름
    @SerializedName("hospital_info_contact_name")
    val hospitalInfoContactName: String,

    // 병원 담당자 연락처
    @SerializedName("hospital_info_contact_phone")
    val hospitalInfoContactPhone: String,


    // 메모(특이사항), nullable
    @SerializedName("hospital_info_memo")
    val hospitalInfoMemo: String? = null,


    // 사용 여부(사용중: 1, 미사용: 0)
    @SerializedName("hospital_info_is_active")
    val hospitalInfoIsActive: Boolean = true,


    // 등록일, nullable
    @SerializedName("hospital_info_created_at")
    val hospitalInfoCreatedAt: String? = null,


    // 관리자(admin) comment
    @SerializedName("hospital_info_admin_comment")
    val hospitalInfoAdminComment: String? = null,

) {
    fun isValid(): Boolean {
        val valid = hospitalInfoName.isNotBlank()
                    && hospitalInfoAddress.isNotBlank()
                    && hospitalInfoContactName.isNotBlank()
                    && hospitalInfoContactPhone.isNotBlank()
        if (!valid) {
            Log.w("HospitalInfo", "Invalid HospitalInfo: hospitalInfoName=$hospitalInfoName, hospitalInfoAddress=$hospitalInfoAddress, hospitalInfoContactName=$hospitalInfoContactName, hospitalInfoContactPhone=$hospitalInfoContactPhone")
        }
        return valid
    }
}
