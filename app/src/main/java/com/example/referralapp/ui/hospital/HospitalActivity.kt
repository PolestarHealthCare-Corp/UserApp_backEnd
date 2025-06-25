package com.example.referralapp.ui.hospital

data class HospitalInfo(
    val hospitalName: String,
    val address: String,
    val contactName: String,
    val contactPhone: String,
    val type: String?,
    val specialties: String?,
    val xrayCount: Int?,
    val ctcount: Int?,
    val mrCount: Int?,
    val mgCount: Int?,
    val memo: String?,
    val agree: Boolean

)