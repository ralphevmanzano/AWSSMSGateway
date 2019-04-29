package com.ralphevmanzano.awssmsgateway

import android.util.Log
import com.ralphevmanzano.awssmsgateway.models.ApiResponse
import com.ralphevmanzano.awssmsgateway.models.User
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ApiService {

    companion object {
        fun create(): ApiService {

            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message: String ->
                Log.d("HttpLogging:", message)
            }).apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .baseUrl("http://192.168.254.155:4000/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @POST("/user/register")
    fun sendToServer(@Body user: User) : Single<ApiResponse>

}