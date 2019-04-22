package com.ralphevmanzano.awssmsgateway

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    companion object Factory {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost:3000/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @GET("/user")
    fun getUsers() : Call<User>

}