package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    //live data login
    private val _loginResult = MutableStateFlow<Result<ResponseLogin>?>(null)
    val loginResult: StateFlow<Result<ResponseLogin>?> = _loginResult

    //loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    //fun login
    suspend fun login(email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
                Log.d("LoginViewModel", "Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}