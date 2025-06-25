package com.example.referralapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("jwt_token")
    val jwtToken: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && phone.isNotBlank()
    }
}