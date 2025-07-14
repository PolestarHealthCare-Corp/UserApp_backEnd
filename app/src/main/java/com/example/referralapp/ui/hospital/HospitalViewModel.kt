package com.example.referralapp.ui.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.referralapp.data.model.HospitalInfo
import com.example.referralapp.data.repository.HospitalRepository
import com.example.referralapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val hospitalName = MutableLiveData<String>()
    val hospitalAddress = MutableLiveData<String>()
    val hospitalContactName = MutableLiveData<String>()
    val hospitalContactPhone = MutableLiveData<String>()
    val hospitalMemo = MutableLiveData<String?>()


    // 병원 등록 상태 관리
    private val _registerState = MutableLiveData<HospitalUiState>(HospitalUiState.Idle)
    val registerState: LiveData<HospitalUiState> = _registerState

    /**
     * Activity에서 전달받은 병원 정보 입력값 저장 후 등록 시도
     */
    fun submitHospitalInfo(
        hospitalName: String,
        address: String,
        contactName: String,
        contactPhone: String,
        memo: String?
    ){
        this.hospitalName.value = hospitalName
        this.hospitalAddress.value = address
        this.hospitalContactName.value = contactName
        this.hospitalContactPhone.value = contactPhone
        this.hospitalMemo.value = memo

        registerHospital()
    }

    /**
     *  병원 정보 서버에 등록
     */
    private fun registerHospital() {
        val userPhone = userRepository.getCurrentUser()?.memberPhone.orEmpty()    // User phone number

        val hospitalName = hospitalName.value.orEmpty()
        val address = hospitalAddress.value.orEmpty()
        val contactName = hospitalContactName.value.orEmpty()
        val contactPhone = hospitalContactPhone.value.orEmpty()
        val memo = hospitalMemo.value

        // 입력값 검증
        if (!validateInput(hospitalName, address, contactName, contactPhone)) return

        _registerState.value = HospitalUiState.Loading

        viewModelScope.launch {
            val hospitalInfo = HospitalInfo(
                memberPhone = userPhone,
                hospitalInfoName = hospitalName,
                hospitalInfoAddress = address,
                hospitalInfoContactName = contactName,
                hospitalInfoContactPhone = contactPhone,
                hospitalInfoMemo = memo
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

    private fun validateInput(
        hospitalName: String,
        address: String,
        contactName: String,
        contactPhone: String
    ) : Boolean {
        return when {
            hospitalName.isBlank() -> {
                _registerState.value = HospitalUiState.Error("병원명을 입력해주세요.")
                false
            }
            address.isBlank() -> {
                _registerState.value = HospitalUiState.Error("주소를 입력해주세요.")
                false
            }
            contactName.isBlank() -> {
                _registerState.value = HospitalUiState.Error("담당자 이름을 입력해주세요.")
                false
            }
            contactPhone.isBlank() -> {
                _registerState.value = HospitalUiState.Error("담당자 연락처를 입력해주세요.")
                false
            }
            else -> true
        }
    }

    fun refresh() {
        _registerState.value = HospitalUiState.Idle
    }
}