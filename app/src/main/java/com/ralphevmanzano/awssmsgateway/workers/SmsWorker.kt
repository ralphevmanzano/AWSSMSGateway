package com.ralphevmanzano.awssmsgateway.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.utils.*

class SmsWorker(ctx: Context, workerParams: WorkerParameters) : Worker(ctx, workerParams) {
  
  override fun doWork(): Result {
    return try {
      val data = inputData.getString(SMS_WORKER_INPUT_KEY)
      val messages = Gson().fromJson(data, SmsEntity::class.java)
      sendSms(messages)
      Result.success()
    } catch (e: Exception) {
      Log.e("Sms", e.toString())
      Result.failure()
    }
  }

  private fun sendSms(msg: SmsEntity) {
    try {
      Log.d("SmsWorker", "Sending message ${msg.id}....")
      val sentIntent = Intent(SENT_ACTION)
      sentIntent.putExtra(SENT_INTENT_EXTRA, msg.id)
      val sentPI = PendingIntent.getBroadcast(applicationContext, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

      val deliveredIntent = Intent(DELIVERED_ACTION)
      deliveredIntent.putExtra(DELIVERED_INTENT_EXTRA, msg.id)
      val deliveredPI = PendingIntent.getBroadcast(applicationContext, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

      val smsManager = SmsManager.getDefault()
      smsManager.sendTextMessage(msg.num, null, msg.message, sentPI, deliveredPI)
    } catch (e: Exception) {
      Log.e("Sms", e.toString())
    }
  }
}