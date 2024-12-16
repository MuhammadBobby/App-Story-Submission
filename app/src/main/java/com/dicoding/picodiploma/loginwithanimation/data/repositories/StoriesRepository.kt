package com.dicoding.picodiploma.loginwithanimation.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.source.PagingStoriesSource
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem
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

    suspend fun getListStories(location: Int? = null,page: Int? = null, size: Int? = null): Result<ResponseListStory> {
        return try {
            // Cek apakah token tersedia
            val token = userPreference.getSession().firstOrNull()?.token
            if (token.isNullOrEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // Panggil API
            val response = apiService.getStories("Bearer $token", page, size, location)

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

        fun getListStoriesPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PagingStoriesSource(storiesRepository = this)
            }
        ).liveData
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


    suspend fun getListPaging(
        location: Int? = null,
        page: Int? = null,
        size: Int? = null
    ): List<ListStoryItem> {
        return try {
            // Ambil token dari UserPreference
            val token = userPreference.getSession().firstOrNull()?.token
                ?: throw Exception("Token not found")

            // Panggil API
            val response = apiService.getStories("Bearer $token", page, size, location)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Pastikan elemen null dihapus
                    body.listStory?.filterNotNull() ?: emptyList()
                } else {
                    throw Exception("Response body is null")
                }
            } else {
                // Tangani kesalahan respons
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                throw Exception("Stories Error with code: ${response.code()}, message: $errorMessage")
            }
        } catch (e: Exception) {
            // Lempar ulang exception agar PagingSource dapat menampilkan error
            throw Exception("Failed to fetch stories: ${e.message}", e)
        }
    }



    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", this.name, requestBody)
    }
}