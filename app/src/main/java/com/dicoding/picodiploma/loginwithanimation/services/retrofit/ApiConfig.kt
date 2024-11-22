package com.dicoding.picodiploma.loginwithanimation.services.retrofit

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(token: String=""): ApiService {
            // Log request and response
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Create an AuthInterceptor to add Bearer token to every request
//            val authInterceptor = { chain: okhttp3.Interceptor.Chain ->
//                val originalRequest: Request = chain.request()
//                val newRequest = originalRequest.newBuilder()
//                    .addHeader("Authorization", "Bearer $token") // Add the Bearer token here
//                    .build()
//                chain.proceed(newRequest)
//            }

            // Create OkHttpClient and add the interceptors
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // For logging
//                .addInterceptor(authInterceptor)    // For Bearer token
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
