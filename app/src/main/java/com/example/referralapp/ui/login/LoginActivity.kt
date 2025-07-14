// LoginActivity.kt
package com.example.referralapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() Called...")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        observeViewModel()
    }

    private fun initView() {
        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.name.value = s.toString()
                updateLoginButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.phone.value = s.toString()
                updateLoginButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnLogin.setOnClickListener {
            Log.d(TAG, "로그인 btn 클릭됨...")
            viewModel.login()
        }
    }

    private fun updateLoginButtonState() {
        val name = binding.editName.text.toString()
        val phone = binding.editPhone.text.toString()
        binding.btnLogin.isEnabled = name.isNotBlank() && phone.isNotBlank()
    }

    private fun observeViewModel() {
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
                    Log.d(TAG, "로그인 성공 : HospitalActivity로 이동")
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HospitalActivity::class.java))
                    finish()
                }

                is LoginUiState.Error -> {
                    Log.e(TAG, "로그인 실패 : ${state.message}")
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
