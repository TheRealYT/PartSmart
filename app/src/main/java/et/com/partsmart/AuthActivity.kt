package et.com.partsmart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import et.com.partsmart.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    internal lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val screen = intent.getStringExtra("screen") ?: "login"
        val fragment = if (screen == "register") RegisterFragment() else LoginFragment()

        supportFragmentManager.beginTransaction()
            .replace(binding.authFragmentContainer.id, fragment)
            .commit()
    }
}