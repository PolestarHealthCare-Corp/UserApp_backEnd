package com.example.referralapp.ui.hospital

sealed class HospitalUiState {
    object Idle : HospitalUiState()
    object Loading : HospitalUiState()
    object Success : HospitalUiState()
    data class Error(val message: String) : HospitalUiState()
}
