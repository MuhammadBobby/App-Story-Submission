package com.dicoding.picodiploma.loginwithanimation.data.repositories

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseAddStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseDetailStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseListStory
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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


    //add
    suspend fun addNewStory(
        description: String,
        imageFile: File,
        lat: Double? = null,
        lon: Double? = null
    ): Result<ResponseAddStory> {
        return try {
            // Ambil token dari session
            val token = userPreference.getSession().firstOrNull()?.token
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token not found"))
            }

            // Konversi file dan data menjadi multipart
            val imagePart = imageFile.toMultipartBody()
            val descriptionPart = description.toRequestBody()
            val latPart = lat?.toString()?.toRequestBody()
            val lonPart = lon?.toString()?.toRequestBody()

            // Panggil API
            val response = apiService.addNewStory("Bearer $token", imagePart, descriptionPart, latPart, lonPart)
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
            // Tangani error
            Result.failure(e)
        }
    }

    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", this.name, requestBody)
    }
}