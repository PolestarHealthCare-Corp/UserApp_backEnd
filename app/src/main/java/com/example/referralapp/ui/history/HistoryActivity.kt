package com.example.referralapp.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.referralapp.databinding.ActivityHistoryBinding
import com.example.referralapp.ui.adapter.HospitalAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val adapter = HospitalAdapter()

    // ViewModel 주입
    private val viewModel: HospitalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        binding.recyclerView.adapter = adapter

        // ViewModel 상태 관찰
        observeHistoryState()
    }

    private fun observeHistoryState() {
        viewModel.historyState.observe(this, Observer { state ->
            when (state) {
                is HistoryUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textEmpty.visibility = View.GONE
                }

                is HistoryUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textEmpty.visibility = View.GONE
                    adapter.submitList(state.hospitals)
                }

                is HistoryUiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = "등록된 소개 내역이 없습니다."
                }

                is HistoryUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = "오류 발생: ${state.message}"
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
