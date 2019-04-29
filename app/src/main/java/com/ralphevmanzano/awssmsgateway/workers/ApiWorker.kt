package com.ralphevmanzano.awssmsgateway.workers

import android.content.Context
import android.util.Log
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.ApiService
import com.ralphevmanzano.awssmsgateway.models.User
import com.ralphevmanzano.awssmsgateway.utils.WORKER_INPUT_DATA
import io.reactivex.Single

class ApiWorker(ctx: Context, params: WorkerParameters) : RxWorker(ctx, params) {

    private val apiService = ApiService.create()

    override fun createWork(): Single<Result> {
        val data = inputData.getString(WORKER_INPUT_DATA)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        return apiService.sendToServer(user)
            .doOnSuccess { Log.d("ApiWorker", "response $it")}
            .map { Result.success() }
            .doOnError { Log.e("ApiWorker", "error ${it.localizedMessage}")}
            .onErrorReturn { Result.failure() }
    }
}