package com.example.referralapp.ui.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.referralapp.data.model.HospitalInfo
import com.example.referralapp.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository
) : ViewModel() {

    val hospitalName = MutableLiveData<String>()
    val hospitalAddress = MutableLiveData<String>()
    val hospitalPhone = MutableLiveData<String>()

    // 병원 등록 상태 관리
    private val _registerState = MutableLiveData<HospitalUiState>(HospitalUiState.Idle)
    val registerState: LiveData<HospitalUiState> = _registerState

    fun registerHospital() {
        val name = hospitalName.value.orEmpty()
        val address = hospitalAddress.value.orEmpty()
        val phone = hospitalPhone.value.orEmpty()

        // 입력값 검증
        if (!validateInput(name, address, phone)) return

        _registerState.value = HospitalUiState.Loading

        viewModelScope.launch {
            val hospitalInfo = HospitalInfo(
                name = name,
                address = address,
                phone = phone
            )

            hospitalRepository.registerHospital(hospitalInfo)
                .onSuccess {
                    _registerState.value = HospitalUiState.Success
                }
                .onFailure { exception ->
                    _registerState.value = HospitalUiState.Error(exception.message ?: "등록에 실패했습니다")
                }
        }
    }

    private fun validateInput(name: String, address: String, phone: String): Boolean {
        return when {
            name.isBlank() -> {
                _registerState.value = HospitalUiState.Error("병원명을 입력해주세요")
                false
            }
            address.isBlank() -> {
                _registerState.value = HospitalUiState.Error("주소를 입력해주세요")
                false
            }
            phone.isBlank() -> {
                _registerState.value = HospitalUiState.Error("전화번호를 입력해주세요")
                false
            }
            else -> true
        }
    }
}