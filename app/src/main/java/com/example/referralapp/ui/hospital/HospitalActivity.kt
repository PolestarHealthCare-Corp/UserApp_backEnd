package com.example.referralapp.ui.hospital

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.referralapp.databinding.ActivityHospitalBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 뱡원 정보 입력 화면
 *  - 사용자가 병원 정보를 입력하고 등록하는 역할
 *  - ViewModel과 연결되어 Data 처리 및 상태 관리 수행
 */

@AndroidEntryPoint
class HospitalActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "HospitalActivity"
    }

    private lateinit var binding: ActivityHospitalBinding
    private val viewModel: HospitalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "[1] HospitalActivity - onCreate Called...")

        binding = ActivityHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatchers()
        setupAgreementCheckbox()
        setupSubmitButton()
        observeRegisterState()
    }

        // [1] 입력 필드 유효성 검사
        private fun setupTextWatchers() {
            val watcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    validateInputs()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            binding.etHospitalName.addTextChangedListener(watcher)
            binding.etAddress.addTextChangedListener(watcher)
            binding.etContactName.addTextChangedListener(watcher)
            binding.etContactPhone.addTextChangedListener(watcher)
        }

         // [2] 개인정보 동의 여부에 따라 제출 버튼 활성화
        private fun setupAgreementCheckbox() {
            binding.cbAgree.setOnCheckedChangeListener { _, _ ->
                validateInputs()
             }
        }

        // [3] 제출 Btn 클릭 처리
        private fun setupSubmitButton() {
            binding.btnSubmit.setOnClickListener {
                val hospitalName = binding.etHospitalName.text.toString()
                val address = binding.etAddress.text.toString()
                val contactName = binding.etContactName.text.toString()
                val contactPhone = binding.etContactPhone.text.toString()
                val memo = buildString {
                    append("특이사항/메모 : ${binding.etMemo.text}")
                }

                viewModel.submitHospitalInfo(
                    hospitalName = hospitalName,
                    address = address,
                    contactName = contactName,
                    contactPhone = contactPhone,
                    memo = memo
                )
            }
        }

    // [4]. 병원 등록 상태 옵저버
    private fun observeRegisterState() {
        viewModel.registerState.observe(this, Observer { state ->
            when (state) {
                is HospitalUiState.Loading -> {
                    Log.d(TAG, "병원 등록 중 입니다...")
                    binding.btnSubmit.isEnabled = false
                }

                is HospitalUiState.Success -> {
                    Toast.makeText(this, "병원 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }

                is HospitalUiState.Error -> {
                    Toast.makeText(this, "Error has occured: ${state.message}", Toast.LENGTH_SHORT).show()
                    binding.btnSubmit.isEnabled = true
                }

                is HospitalUiState.Idle -> {
                    binding.btnSubmit.isEnabled = binding.cbAgree.isChecked
                    // 아무 작업 없음(초기 상태)
                }
            }
        })
    }

    // [5] 모든 조건 만족 시, 제출 btn 활성화
    private fun validateInputs() {
        val isHospitalNameValid = binding.etHospitalName.text.toString().isNotBlank()
        val isAddressValid = binding.etAddress.text.toString().isNotBlank()
        val isContactNameValid = binding.etContactName.text.toString().isNotBlank()
        val isContactPhoneValid = binding.etContactPhone.text.toString().isNotBlank()
        val isAgreementChecked = binding.cbAgree.isChecked

        val isValid = isHospitalNameValid && isAddressValid && isContactNameValid && isContactPhoneValid && isAgreementChecked

        binding.btnSubmit.isEnabled = isValid

    }

}