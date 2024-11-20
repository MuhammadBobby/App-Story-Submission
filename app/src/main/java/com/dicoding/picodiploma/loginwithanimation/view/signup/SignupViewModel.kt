package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseRegister
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel()  {
    //livedata for  result register
    private val _registerResult = MutableStateFlow<Result<ResponseRegister>?>(null)
    val registerResult: StateFlow<Result<ResponseRegister>?> = _registerResult

    //loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //register
    suspend fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                _registerResult.value = repository.register(name, email, password)
                Log.d("SignupViewModel", "Registration successful")
            } catch (e: Exception) {
                Log.d("SignupViewModel", "Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }

    }

}