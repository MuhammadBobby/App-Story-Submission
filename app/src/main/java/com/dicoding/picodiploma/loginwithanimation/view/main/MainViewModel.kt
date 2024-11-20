package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val storiesRepository: StoriesRepository) : ViewModel() {
    //live data result list
    private val _resultStories = MutableLiveData<List<ListStoryItem>>()
    val resultStories: LiveData<List<ListStoryItem>> = _resultStories

    //loading & error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    //get list stories
    suspend fun getListStories() {
//        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = storiesRepository.getListStories()
                if (response.isSuccess) {
                    val body = response.getOrNull()

                    if (body != null) {
                        _resultStories.value = body.listStory as List<ListStoryItem>
                        Log.d("MainViewModel", "List stories: ${_resultStories.value}")
                    } else {
                        _errorMessage.value = "Response body is null"
                        Log.d("MainViewModel", "Response body is null")
                    }
                } else {
                    _errorMessage.value = "Error: ${response.exceptionOrNull()?.message}"
                    Log.d("MainViewModel", "Error: ${response.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.d("MainViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }

        }

    }


    // Get session
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    // Logout
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}