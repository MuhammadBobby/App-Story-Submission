package com.dicoding.picodiploma.loginwithanimation.data.repositories

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseLogin
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseRegister
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }

    // Register
    suspend fun register(name: String, email: String, password: String): Result<ResponseRegister> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Registration failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //login
    suspend fun login(email: String, password: String): Result<ResponseLogin> {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("login failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Save session
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    // Get session
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    // Logout
    suspend fun logout() {
        userPreference.logout()
    }
}
