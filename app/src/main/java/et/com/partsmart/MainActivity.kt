package et.com.partsmart

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import et.com.partsmart.api.Repository
import et.com.partsmart.databinding.ActivityMainBinding
import et.com.partsmart.view_models.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels() {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Repository.init(this)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val themeMode = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)

        super.onCreate(savedInstanceState)

        if (viewModel.hasLoginSession()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("screen", "login")
            startActivity(intent)
        }

        binding.btnReg.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("screen", "register")
            startActivity(intent)
        }
    }
}