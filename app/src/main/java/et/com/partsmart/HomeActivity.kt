package et.com.partsmart

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import et.com.partsmart.databinding.ActivityHomeBinding
import et.com.partsmart.models.User
import et.com.partsmart.view_models.AuthViewModel
import kotlin.math.abs

interface ManageAppBar {
    fun hide(title: String)

    fun show()
}

class HomeActivity : AppCompatActivity(), ManageAppBar {
    private lateinit var binding: ActivityHomeBinding
    private val authViewModel: AuthViewModel by viewModels() {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private lateinit var token: String
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        val token = authViewModel.getSessionToken()
        val user = authViewModel.getUser()

        if (token == null || user == null) {
            authViewModel.logout()
            return
        }

        this.token = token // token to access endpoints
        this.user = user

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentItems.id, ItemsFragment())
            }
        }

        binding.fab.setOnClickListener {
            Toast.makeText(this, "FAB clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBar, verticalOffset ->
            val totalScrollRange = appBar.totalScrollRange
            val isCollapsed = abs(verticalOffset) == totalScrollRange

            val layoutParams =
                binding.searchInputLayout.layoutParams as ViewGroup.MarginLayoutParams

            if (isCollapsed) {
                // Smaller margins when collapsed
                val marginPx = dpToPx(52)
                layoutParams.setMargins(
                    marginPx,
                    layoutParams.topMargin,
                    marginPx,
                    layoutParams.bottomMargin
                )
            } else {
                // Larger margins when expanded
                val marginPx = dpToPx(16)
                layoutParams.setMargins(
                    marginPx,
                    layoutParams.topMargin,
                    marginPx,
                    layoutParams.bottomMargin
                )
            }

            binding.searchInputLayout.layoutParams = layoutParams
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentItems.id, ItemsFragment())
                    }
                    show()
                    true
                }

                R.id.nav_orders -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentItems.id, OrdersFragment())
                    }
                    true
                }

                R.id.nav_products -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentItems.id, MyProductsFragment())
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                authViewModel.logout()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }

            R.id.menu_theme_dark -> {
                changeTheme(Theme.Dark)
                return true
            }

            R.id.menu_theme_light -> {
                changeTheme(Theme.Light)
                return true
            }

            R.id.menu_theme_system -> {
                changeTheme(Theme.System)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val themePref = getSharedPreferences("settings", MODE_PRIVATE)
            .getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Hide all check icons first
        menu.findItem(R.id.menu_theme_dark).icon = null
        menu.findItem(R.id.menu_theme_light).icon = null
        menu.findItem(R.id.menu_theme_system).icon = null

        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val iconColor = ContextCompat.getColor(this, typedValue.resourceId)

        val checkIcon = AppCompatResources.getDrawable(this, R.drawable.baseline_check_24)?.apply {
            setTint(iconColor)
        }

        when (themePref) {
            AppCompatDelegate.MODE_NIGHT_YES ->
                menu.findItem(R.id.menu_theme_dark).icon = checkIcon

            AppCompatDelegate.MODE_NIGHT_NO ->
                menu.findItem(R.id.menu_theme_light).icon = checkIcon

            else ->
                menu.findItem(R.id.menu_theme_system).icon = checkIcon
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private enum class Theme {
        Dark,
        Light,
        System
    }

    private fun changeTheme(theme: Theme) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val editor = prefs.edit()

        val mode = when (theme) {
            Theme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
        editor.putInt("theme", mode)
        editor.apply()
    }

    override fun hide(title: String) {
        binding.toolbar.title = title
        binding.collapsingToolbarLayout.title = title
        binding.appBarLayout.setExpanded(false, false)
        binding.scrollView.isNestedScrollingEnabled = false

        binding.searchInputLayout.visibility = android.view.View.INVISIBLE
        binding.fragmentTopItems.visibility = android.view.View.INVISIBLE
    }

    override fun show() {
        binding.toolbar.title = " "
        binding.collapsingToolbarLayout.title = ""
        binding.appBarLayout.setExpanded(true, false)
        binding.scrollView.isNestedScrollingEnabled = true

        binding.searchInputLayout.visibility = android.view.View.VISIBLE
        binding.fragmentTopItems.visibility = android.view.View.VISIBLE
    }
}