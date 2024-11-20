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
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, ApiConfig.getApiService(""))
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize adapter
        adapter =MainAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Check session (isLoggedIn) and navigate accordingly
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            }
        }

        // Set up the Toolbar (App Bar)
        setSupportActionBar(binding.toolbar) // Assuming you add a toolbar in XML
        setupView()

        // Set up RecyclerView
        lifecycleScope.launch {
            viewModel.getListStories()
        }
        observeViewModel()
    }

    private fun setupView() {
        // Set full-screen mode (hide the status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


    // Observe
    private fun observeViewModel() {
        //observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //observe error
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        //observe list stories
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
                // Handle Add Story
                addStory()
                true
            }
            R.id.menu_logout -> {
                // Handle Logout
                viewModel.logout()
                navigateToWelcomeActivity() // After logout, navigate to WelcomeActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addStory() {
        // Navigate to Add Story Activity (You can implement this later)
//        val intent = Intent(this, AddStoryActivity::class.java)
//        startActivity(intent)
        Toast.makeText(this, "Add Story button clicked", Toast.LENGTH_SHORT).show()
    }
}
