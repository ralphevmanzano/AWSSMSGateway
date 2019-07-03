package com.ralphevmanzano.awssmsgateway.workers

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.api.ApiService
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.SmsModel
import com.ralphevmanzano.awssmsgateway.models.WeatherDataModel
import com.ralphevmanzano.awssmsgateway.utils.API_WORKER_INPUT_KEY
import io.reactivex.Single

class ApiWorker(ctx: Context, params: WorkerParameters) : RxWorker(ctx, params) {

  private val apiService = ApiService.create()
  private val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
  private val ipKey = ctx.getString(R.string.server_ip)

  override fun createWork(): Single<Result> {
    val data = inputData.getString(API_WORKER_INPUT_KEY)
    val weatherData = Gson().fromJson(data, WeatherDataModel::class.java)
    val ip = pref.getString(ipKey, "192.168.1.15")

    return apiService.sendToServer("http://$ip/", weatherData)
      .retry(3)
      .doOnSuccess { Log.d("ApiWorker", "response $it") }
      .map { Result.success() }
      .doOnError { Log.e("ApiWorker", "error ${it.localizedMessage}") }
      .onErrorReturn { Result.failure() }
  }
}