package et.com.partsmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import et.com.partsmart.api.Repository
import et.com.partsmart.databinding.ActivityHomeBinding
import et.com.partsmart.models.User
import et.com.partsmart.storage.CartDBHelper
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
    internal lateinit var user: User
    private var badgeDrawable: BadgeDrawable? = null

    @OptIn(ExperimentalBadgeUtils::class)
    internal fun updateCartCount() {
        val count = CartDBHelper(this).getCartCount()

        if (badgeDrawable != null)
            BadgeUtils.detachBadgeDrawable(badgeDrawable, binding.fab)

        badgeDrawable = BadgeDrawable.create(this)
        badgeDrawable!!.number = count
        badgeDrawable!!.isVisible = count > 0

        BadgeUtils.attachBadgeDrawable(badgeDrawable!!, binding.fab)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        Repository.init(this)

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
            CartDialogFragment.show(supportFragmentManager)
        }

        binding.fabProduct.setOnClickListener {
            UploadProductFragment.show(supportFragmentManager)
        }

        binding.toolbar.setNavigationOnClickListener {
            AccountDialogFragment.show(supportFragmentManager)
        }

        binding.searchInput.addTextChangedListener {
            if (it != null) {
                val currentFragment =
                    supportFragmentManager.findFragmentById(binding.fragmentItems.id)
                if (currentFragment is ItemsFragment) {
                    currentFragment.filter(it.toString())
                }
            }
        }

        binding.searchInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2  // index for right drawable
                val drawable = binding.searchInput.compoundDrawables[drawableEnd]
                if (drawable != null && event.rawX >= (binding.searchInput.right - drawable.bounds.width())) {
                    binding.searchInput.text?.clear()
                    binding.searchInput.clearFocus()
                    true
                } else false
            } else false
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
                    binding.fabProduct.hide()
                    true
                }

                R.id.nav_orders -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentItems.id, OrdersFragment())
                    }
                    binding.fabProduct.hide()
                    true
                }

                R.id.nav_products -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentItems.id, MyProductsFragment())
                    }
                    binding.fabProduct.show()
                    true
                }

                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (onBack()) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.fab.post {
            updateCartCount()
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

    private fun onBack(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(binding.fragmentItems.id)
        if (currentFragment is OrdersFragment || currentFragment is MyProductsFragment) {
            supportFragmentManager.commit {
                replace(binding.fragmentItems.id, ItemsFragment())
            }
            binding.bottomNav.selectedItemId = R.id.nav_home
            show()

            return false
        }

        return true
    }
}