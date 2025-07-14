package com.example.referralapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.referralapp.data.model.User
import com.example.referralapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val name = MutableLiveData<String>().apply { value = ""}
    val phone = MutableLiveData<String>().apply { value = ""}

    // 명확한 상태 관리
    private val _loginState = MutableLiveData<LoginUiState>(LoginUiState.Idle)
    val loginState: LiveData<LoginUiState> = _loginState

    fun login() {
        val nameValue = name.value.orEmpty().trim()
        val phoneValue = phone.value.orEmpty().trim()
        val roleValue = 1

        // 입력값 검증
        if (!validateInput(nameValue, phoneValue)) return

        _loginState.value = LoginUiState.Loading

        viewModelScope.launch {
            val user = User(memberName = nameValue, memberPhone = phoneValue, memberRole = roleValue, memberIsActive = true)

            userRepository.loginOrRegister(user)
                .onSuccess {
                    _loginState.value = LoginUiState.Success
                }
                .onFailure { exception ->
                    val message = exception.message ?: "로그인 실패했습니다."
                    _loginState.value = LoginUiState.Error(message)
                }
            }
        }

    /** 입력 유효성 검사   **/
    private fun validateInput(name: String, phone: String): Boolean {
        return when {
            name.isBlank() -> {
                _loginState.value = LoginUiState.Error("이름을 입력해주세요.")
                false
            }
            phone.isBlank() -> {
                _loginState.value = LoginUiState.Error("연락처를 입력해주세요.")
                false
            }
            !isValidPhoneNumber(phone) -> {
                _loginState.value = LoginUiState.Error("연락처를 다시 확인해주세요.")
                false
            }
            else -> true
        }
    }

    /** 연락처 형식 검사 (010xxxxxxxx 또는 010-xxxx-xxxx 허용) **/
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^01[016789]-?\\d{3,4}-?\\d{4}$"))
    }
}

/** UI 상태 클래스 **/
sealed class LoginUiState {
    object Idle : LoginUiState()        // 초기 상태
    object Loading : LoginUiState()     // 로딩 중
    object Success : LoginUiState()     // 성공
    data class Error(val message: String) : LoginUiState()      // 에러 + msg
}