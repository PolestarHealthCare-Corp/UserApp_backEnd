package com.example.referralapp.data.model

import com.google.gson.annotations.SerializedName

data class HospitalInfo(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("speciality")
    val speciality: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("registered_by")
    val registeredBy: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && address.isNotBlank() && phone.isNotBlank()
    }
}
