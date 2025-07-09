package com.example.referralapp.data.model

import android.util.Log
import com.google.gson.annotations.SerializedName

data class User(

    // 사용자(User) 이름
    @SerializedName("member_name")
    val memberName: String,

    // 사용자(User) 연락처
    @SerializedName("member_phone")
    val memberPhone: String,

    // 회원유형
    @SerializedName("member_role")
    val memberRole: Int,                    // 0: Admin, 1: User

    // 사용 여부
    @SerializedName("member_is_active")
    val memberIsActive: Boolean,

    // 생성일
    @SerializedName("member_created_at")
    val memberCreatedAt: String? = null,

    // JWT Token
    @SerializedName("jwtToken")
    val jwtToken: String? = null,

) {
    fun isValid(): Boolean {
        val valid = memberName.isNotBlank()
                && memberPhone.isNotBlank()
        if (!valid) {
            Log.w("User", "Invalid User info: memberName=$memberName, memberPhone=$memberPhone")
        }
        return valid
    }
}