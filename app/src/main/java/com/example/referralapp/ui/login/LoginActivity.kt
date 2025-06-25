package com.example.referralapp.ui.login

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding,btnLogin.setOnClickListener {
            viewModel.login()
        }

        viewModel.loginState.observe(this) {
            if (it) {
                startActivity(Intent(this, HospitalActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }
}