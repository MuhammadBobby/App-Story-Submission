package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storiesRepository: StoriesRepository
) : ViewModel() {

    // LiveData untuk daftar cerita
    private val _resultStories = MutableLiveData<List<ListStoryItem>>()
    val resultStories: LiveData<List<ListStoryItem>> = _resultStories

    // LiveData untuk status loading dan error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Fungsi untuk mendapatkan daftar cerita
    fun getListStories() {
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
                // Mengambil data cerita
                val response = storiesRepository.getListStories()
                response.fold(
                    onSuccess = { body ->
                        _resultStories.value = body.listStory as List<ListStoryItem>
                        Log.d("MainViewModel", "List stories: ${_resultStories.value}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error: ${exception.message}"
                        Log.d("MainViewModel", "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
                Log.d("MainViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mendapatkan sesi pengguna
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    // Fungsi untuk logout
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
