package et.com.partsmart

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import et.com.partsmart.databinding.ActivityHomeBinding
import et.com.partsmart.models.CardItem
import et.com.partsmart.models.GridItem
import et.com.partsmart.models.User
import et.com.partsmart.view_models.AuthViewModel
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {
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

            R.id.menu_toggle_dark -> {
                toggleTheme()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val editor = prefs.edit()

        when (currentMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        editor.apply()
    }
}

class CardAdapter(private val items: List<CardItem>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val subtitle: TextView = itemView.findViewById(R.id.cardSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
    }

    override fun getItemCount() = items.size
}

class GridAdapter(private val items: List<GridItem>) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.gridTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.title.text = items[position].title
    }

    override fun getItemCount() = items.size
}