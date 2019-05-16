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
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface ApiService {

  companion object {
    fun create(): ApiService {

      val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message: String ->
        Log.d("HttpLogging:", message)
      }).apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
      }

      val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
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

  @POST
  fun sendToServer(@Url url: String?, @Body user: User): Single<ApiResponse>

}