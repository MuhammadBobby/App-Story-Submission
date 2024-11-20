package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object Injection {

    // Provide UserRepository
    fun provideRepository(context: Context, apiService: ApiService): UserRepository {
        val userPreference = UserPreference.getInstance(context)
        return UserRepository.getInstance(userPreference, apiService)
    }

    // Provide StoriesRepository with Bearer Token
    fun provideStoriesRepository(context: Context): StoriesRepository {
        val userPreference = UserPreference.getInstance(context)

        // Getting user session and the token
        val user = runBlocking { userPreference.getSession().firstOrNull() }
        val token = user?.token ?: ""
        val apiService = ApiConfig.getApiService(token)
        return StoriesRepository.getInstance(apiService, userPreference)
    }
}
