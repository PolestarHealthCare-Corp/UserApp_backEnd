// LoginActivity.kt
package com.example.referralapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.referralapp.databinding.ActivityLoginBinding
import com.example.referralapp.ui.hospital.HospitalActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * LoginActivity는 사용자 간편 로그인 화면을 담당하는 Activity입니다.
 * 이름과 전화번호를 입력받아 ViewModel을 통해 로그인 처리 후,
 * 병원 소개 화면(HospitalActivity)으로 이동합니다.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "[LoginActivity] onCreate")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 사용자 입력값과 ViewModel 연결
        binding.editName.addTextChangedListener {
            viewModel.name.value = it.toString()
        }
        binding.editPhone.addTextChangedListener {
            viewModel.phone.value = it.toString()
        }

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            Log.d(TAG, "[LoginActivity] 로그인 버튼 클릭")
            viewModel.login()
        }

        // 로그인 상태 관찰
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginUiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
                is LoginUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is LoginUiState.Success -> {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HospitalActivity::class.java))
                    finish()
                }
                is LoginUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
