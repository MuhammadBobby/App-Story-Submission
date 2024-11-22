package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object Injection {

    // Provide UserRepository
    fun provideRepository(context: Context, apiService: ApiService = ApiConfig.getApiService("")): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference, apiService)
    }

    // Provide StoriesRepository with Bearer Token
    fun provideStoriesRepository(context: Context): StoriesRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoriesRepository.getInstance(apiService, userPreference)
    }
}
