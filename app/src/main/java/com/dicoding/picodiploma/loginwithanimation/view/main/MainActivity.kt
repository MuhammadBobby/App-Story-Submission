package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.add.AddActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, )
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter
        adapter = MainAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        userPreference = UserPreference.getInstance(this.dataStore)

        // Observe session (isLoggedIn)
        observeSession()

        // Set up the Toolbar (App Bar)
        setSupportActionBar(binding.toolbar) // Assuming you add a toolbar in XML
        setupView()

        // Observe ViewModel for loading, error, and stories
        observeViewModel()
    }

    private fun setupView() {
        // Set full-screen mode (hide the status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.token.isEmpty()) {
                // Redirect to WelcomeActivity if not logged in or token is invalid
                navigateToWelcomeActivity()
            } else {
                // Fetch stories if logged in
                fetchStories()
            }
        }
    }

    private fun fetchStories() {
        lifecycleScope.launch {
            viewModel.getListStories()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe list of stories
        viewModel.resultStories.observe(this) { listStories ->
            if (listStories != null) {
                adapter.setDataStories(listStories)
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity so the user cannot go back
        return
    }

    // Inflate the menu for the Toolbar (App Bar)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle menu item clicks (e.g., Add Story and Logout)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_story -> {
                addStory()
                true
            }
            R.id.menu_logout -> {
                viewModel.logout()
                navigateToWelcomeActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addStory() {
        // TODO: Implement Add Story navigation
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }
}
