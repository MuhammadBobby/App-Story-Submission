package com.dicoding.picodiploma.loginwithanimation.data.repositories

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseDetailStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseListStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.Story
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

class StoriesRepository(private val apiService: ApiService, private val userPreference: UserPreference) {
    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoriesRepository {
            return StoriesRepository(apiService, userPreference)
        }
    }

    suspend fun getListStories(): Result<ResponseListStory> {
        return try {
            // Cek apakah token tersedia
            val token = userPreference.getSession().firstOrNull()?.token
            if (token.isNullOrEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // Panggil API
            val response = apiService.getStories("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                // Log response error untuk memudahkan debugging
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Stories Error with code: ${response.code()}, message: $errorMessage"))
            }
        } catch (e: Exception) {
            // Log exception untuk debugging
            Result.failure(e)
        }
    }

    //get detail
    suspend fun getDetailStory(id: String): Result<ResponseDetailStory> {
        return try {
            //cek token ready or not
            val token = userPreference.getSession().firstOrNull()?.token
            if (token.isNullOrEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            val response = apiService.getDetailStory("Bearer $token", id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Stories Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}