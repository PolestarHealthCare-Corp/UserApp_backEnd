package com.example.referralapp.ui.hospital

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.referralapp.databinding.ActivityHospitalBinding

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
        Log.d(TAG, "[1] HospitalActivity 실행됨...")

        binding = ActivityHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [2] 등록 button 클릭 시 ViewModel로 전달
        binding.btnSubmit.setOnClickListener {
            Log.d(TAG, "[2] 등록 button 클릭...")
            viewModel.submitHospitalInfo(
                hospitalName = binding.editHospitalName.text.toString(),
                address = binding.editAddress.text.toString(),
                contactName = binding.editContactName.text.toString(),
                contactphone = binding.editContactPhone.text.toString(),
                email = binding.editEmail.text.toString(),
                speciality = binding.editSpeciality.text.toString(),
                description = binding.editDescription.text.toString()

            )
        }
    }
}