package com.dicoding.picodiploma.loginwithanimation.services.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(): ApiService {
            // Log request and response
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Create OkHttpClient and add the interceptors
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // For logging
                .build()

            // Create Retrofit instance with base URL
            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/") // Your base API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Return the ApiService instance
            return retrofit.create(ApiService::class.java)
        }
    }
}
