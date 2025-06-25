package com.example.referralapp.ui.history

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val adapter = HospitalAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            val result = ApiClient.api.getMyHospitalList()
            if (result.isSuccessful) {
                adapter.submitList(result.body())
            }
        }
    }
}