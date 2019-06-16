package com.ralphevmanzano.awssmsgateway.workers

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.utils.SMS_WORKER_INPUT_KEY

class SmsWorker(ctx: Context, workerParams: WorkerParameters) : Worker(ctx, workerParams) {
  override fun doWork(): Result {
    return try {
      val data = inputData.getString(SMS_WORKER_INPUT_KEY)
      val messages = Gson().fromJson(data, Array<SmsEntity>::class.java).toList()

      for (msg in messages) {
        sendSms(msg)
      }

      Result.success()
    } catch (e: Exception) {
      Log.e("Sms", e.localizedMessage)
      Result.failure()
    }
  }

  private fun sendSms(msg: SmsEntity) {
    try {
      val smsManager = SmsManager.getDefault()
      smsManager.sendTextMessage(msg.num, null, msg.message, null, null)
    } catch (e: Exception) {
      Log.e("Sms", e.localizedMessage)
    }
  }
}