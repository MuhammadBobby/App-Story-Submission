package com.dicoding.picodiploma.loginwithanimation.services.retrofit

import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseListStory
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseLogin
import com.dicoding.picodiploma.loginwithanimation.services.responses.ResponseRegister
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ) : Response<ResponseListStory>

}