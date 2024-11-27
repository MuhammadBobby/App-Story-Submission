package com.dicoding.picodiploma.loginwithanimation.services.retrofit

import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseAddStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseDetailStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseListStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseLogin
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //register service
    @FormUrlEncoded
    @POST("register")
    suspend fun register (
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<ResponseRegister>

    //login service
    @FormUrlEncoded
    @POST("login")
    suspend fun login (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<ResponseLogin>

    //list story
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ) : Response<ResponseListStory>

    //detail story
    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Response<ResponseDetailStory>

    //add
    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ) : Response<ResponseAddStory>


}