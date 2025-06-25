package com.example.referralapp.ui.history

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

    // 소개 내역 상태 관리
    private val _historyState = MutableLiveData<HistoryUiState>(HistoryUiState.Loading)
    val historyState: LiveData<HistoryUiState> = _historyState

    init {
        loadHistory()
    }

    fun loadHistory() {
        _historyState.value = HistoryUiState.Loading

        viewModelScope.launch {
            hospitalRepository.getReferralHistory()
                .onSuccess { hospitals ->
                    _historyState.value = if (hospitals.isNotEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Success(hospitals)
                    }
                }
                .onFailure { exception ->
                    _historyState.value = HistoryUiState.Error(exception.message ?: "조회에 실패했습니다.")
                }
        }
    }

    fun refresh() {
        loadHistory()
    }

}

// UI State Classes
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

sealed class HospitalUiState {
    object Idle : HospitalUiState()
    object Loading : HospitalUiState()
    object Success : HospitalUiState()
    data class Error(val message: String) : HospitalUiState()
}

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    object Empty : HistoryUiState()
    data class Success(val hospitals: List<HospitalInfo>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}
