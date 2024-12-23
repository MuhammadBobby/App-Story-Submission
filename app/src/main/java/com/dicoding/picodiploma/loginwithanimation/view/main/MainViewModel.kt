package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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

    // LiveData untuk detail story
    private val _resultMaps = MutableLiveData<List<ListStoryItem>>()
    val resultMaps: LiveData<List<ListStoryItem>> = _resultMaps

    // LiveData untuk status loading dan error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    //live data from paging source
    val pagingStories: LiveData<PagingData<ListStoryItem>> =
        storiesRepository.getListStoriesPaging().cachedIn(viewModelScope)


    //fun get maps
    fun getMapsStories() {
        _isLoading.value = true

        viewModelScope.launch {
            val session = userRepository.getSession().firstOrNull()
            if (session == null || session.token.isEmpty()) {
                _errorMessage.value = "User not logged in or token missing."
                _isLoading.value = false
                return@launch
            }

            try {
                val response = storiesRepository.getListStories(1)
                response.fold(
                    onSuccess = { body ->
                        _resultMaps.value = body.listStory as List<ListStoryItem>
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
