package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_ID_STORY = "EXTRA_ID_STORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get id story
        val idStory = intent.getStringExtra(EXTRA_ID_STORY)
        if (idStory != null) {
            lifecycleScope.launch {
                viewModel.getDetailStory(idStory)
            }
        }

        //observe
        observeViewModel()
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        //observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //observe error
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        //observe detail story
        viewModel.resultDetail.observe(this) { detailStory ->
            if (detailStory != null) {
                binding.tvDetailName.text = detailStory.story?.name
                binding.tvDetailDescription.text = detailStory.story?.description
                binding.storyCreatedAt.text = detailStory.story?.createdAt?.let { formatDate(it) }

                //set image
                Glide.with(this)
                    .load(detailStory.story?.photoUrl)
                    .apply(RequestOptions().transform(RoundedCorners(14)))
                    .into(binding.ivDetailPhoto)
            }
        }
    }


    fun formatDate(isoString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Atur timezone UTC

        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) // Format output: 8 January 2022

        val date = inputFormat.parse(isoString)
        return outputFormat.format(date ?: Date()) // Mengembalikan hasil yang terformat
    }
}