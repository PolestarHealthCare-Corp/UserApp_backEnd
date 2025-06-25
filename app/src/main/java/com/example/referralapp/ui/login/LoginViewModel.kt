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

    val name = MutableLiveData<String>()
    val phone = MutableLiveData<String>()

    // 명확한 상태 관리
    private val _loginState = MutableLiveData<LoginUiState>(LoginUiState.Idle)
    val loginState: LiveData<LoginUiState> = _loginState

    fun login() {
        val nameValue = name.value.orEmpty()
        val phoneValue = phone.value.orEmpty()

        // 입력값 검증
        if (!validateInput(nameValue, phoneValue)) return

        _loginState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val user = User(nameValue, phoneValue)
                val result = userRepository.loginOrRegister(user)
                _loginState.value = if (result) {
                    LoginUiState.Success
                } else {
                    LoginUiState.Error("로그인에 실패했습니다")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    private fun validateInput(name: String, phone: String): Boolean {
        return when {
            name.isBlank() -> {
                _loginState.value = LoginUiState.Error("이름을 입력해주세요")
                false
            }
            phone.isBlank() -> {
                _loginState.value = LoginUiState.Error("전화번호를 입력해주세요")
                false
            }
            !isValidPhoneNumber(phone) -> {
                _loginState.value = LoginUiState.Error("연락처를 다시 확인해주세요.")
                false
            }
            else -> true
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^01[016789]-?\\d{3,4}-?\\d{4}$"))
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()        // 초기 상태
    object Loading : LoginUiState()     // 로딩 중
    object Success : LoginUiState()     // 성공
    data class Error(val message: String) : LoginUiState()      // 에러 + msg
}