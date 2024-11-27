package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseDetailStory
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoriesRepository) : ViewModel() {
    //livedata detail result
    private val _resultDetail = MutableLiveData<ResponseDetailStory?>()
    val resultDetail: LiveData<ResponseDetailStory?> = _resultDetail

    //loading & error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    //get detail
    suspend fun getDetailStory(id: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getDetailStory(id)
                if (response.isSuccess) {
                    val body = response.getOrNull()
                    if (body != null) {
                        _resultDetail.value = body
                    } else {
                        _errorMessage.value = "Response body is null"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

    }
}