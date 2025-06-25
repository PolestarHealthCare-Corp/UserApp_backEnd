package com.example.referralapp.data.model

import android.util.Log
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("jwtToken")
    val jwtToken: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
) {
    fun isValid(): Boolean {
        val valid = name.isNotBlank() && phone.isNotBlank()
        if (!valid) {
            Log.w("User", "Invalid User info: name=$name, phone=$phone")
        }
        return valid
    }
}