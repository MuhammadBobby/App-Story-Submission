package com.dicoding.picodiploma.loginwithanimation.data.repositories

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseListStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.Story
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull

class StoriesRepository(private val apiService: ApiService, private val userPreference: UserPreference) {
    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoriesRepository {
            return StoriesRepository(apiService, userPreference)
        }
    }

    //list stories
    suspend fun getListStories(): Result<ResponseListStory> {
        return try {
            val token = userPreference.getSession().firstOrNull()?.token ?: throw Exception("Token not found")

            val response = apiService.getStories()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Stories Error with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}