package com.dicoding.picodiploma.loginwithanimation.view.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseAddStory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class AddViewModel(private val storiesRepository: StoriesRepository, private val userRepository: UserRepository) : ViewModel()  {
    //live data result
    private val _resultAddStory = MutableLiveData<ResponseAddStory>()
    val resultAddStory: LiveData<ResponseAddStory> = _resultAddStory

    //live data loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //live data error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    //fun add story
    suspend fun addNewstory(file: File, description: String) {
        _isLoading.value = true

        viewModelScope.launch {
            // Validasi sesi sebelum melakukan operasi
            val session = userRepository.getSession().firstOrNull()
            if (session == null || session.token.isEmpty()) {
                _errorMessage.value = "User not logged in or token missing."
                _isLoading.value = false
                return@launch
            }
            try {
                //send data to repo
                val response = storiesRepository.addNewStory(description, file)

                response.fold(
                    onSuccess = { body ->
                        _resultAddStory.value = body
                        Log.d("MainViewModel", "List stories: ${_resultAddStory.value}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error: ${exception.message}"
                        Log.d("MainViewModel", "Error: ${exception.message}")
                    }
                )

            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}