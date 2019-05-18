package com.ralphevmanzano.awssmsgateway

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.models.SmsModel
import com.ralphevmanzano.awssmsgateway.utils.WORKER_INPUT_DATA
import com.ralphevmanzano.awssmsgateway.workers.ApiWorker
import java.util.*
import java.util.concurrent.TimeUnit


class SmsBroadcastReceiver : BroadcastReceiver() {
  private val TAG = "SmsBroadcastReceiver"

  private var smsListener: SmsListener? = null
  private var notificationManager: NotificationManager? = null


  companion object {
    const val DEFAULT_CHANNEL_NAME = "SmsModel Notifications"
    const val DEFAULT_CHANNEL_ID = "sms_notification_id"
  }

  private lateinit var context: Context

  override fun onReceive(context: Context, intent: Intent) {
    this.context = context

    if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
      var smsSender: String? = null
      var smsBody = ""
      var smsTimestamp: Long? = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
          smsSender = smsMessage.displayOriginatingAddress
          smsBody += smsMessage.messageBody
          smsTimestamp = smsMessage.timestampMillis
        }
      } else {
        val smsBundle = intent.extras
        if (smsBundle != null) {
          val pdus = smsBundle.get("pdus") as Array<*>
          if (pdus == null) {
            // Display some error to the user
            Log.e(TAG, "SmsBundle had no pdus key")
            return
          }
          val messages = arrayOfNulls<SmsMessage>(pdus.size)
          for (i in messages.indices) {
            messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            smsBody += messages[i]!!.messageBody
          }
          smsSender = messages[0]?.originatingAddress!!
          smsTimestamp = messages[0]!!.timestampMillis
        }
      }

      val sms = safeLet(smsSender, smsBody, smsTimestamp?.let { millisToDate(it) }) { sender, body, date ->
        SmsModel(sender, body, date)
      }

      Log.d("Receiver", "SmsModel Received!! \n ${sms?.number} \t ${sms?.message} \t ${sms?.timestamp}")

      sms?.let {
        if (it.message.startsWith("(", true) &&
            it.message.endsWith(")", true)) {
          if (smsListener != null) {
            Log.d("Receiver", smsBody)
            smsListener!!.onTextReceived(sms)
          }

          startApiWork(sms)
          initNotification()
        }
      }
    }
  }

  private fun startApiWork(sms: SmsModel?) {
    //intent service or workmanager
    val data = Gson().toJson(sms)

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val apiWorkerRequest = OneTimeWorkRequestBuilder<ApiWorker>()
      .setConstraints(constraints)
      .setInputData(workDataOf(WORKER_INPUT_DATA to data))
      .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS)
      .build()

    WorkManager.getInstance().enqueue(apiWorkerRequest)
  }

  private fun millisToDate(currentTime: Long): String {
    val finalDate: String
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = currentTime
    val date = calendar.time
    finalDate = date.toString()
    return finalDate
  }

  internal fun setListener(smsListener: SmsListener?) {
    this.smsListener = smsListener
  }

  private fun initNotification() {
    notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel()

    val notification = NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
      .setContentTitle("Test")
      .setContentText("This is a test notification")
      .setSmallIcon(android.R.drawable.ic_menu_view)
      .build()

    //Send the notification.
    notificationManager!!.notify(1, notification)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      //Create channel only if it is not already created
      if (notificationManager?.getNotificationChannel(DEFAULT_CHANNEL_ID) == null) {
        notificationManager?.createNotificationChannel(
          NotificationChannel(
            DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
          )
        )
      }
    }
  }

  fun <T1: Any, T2: Any, T3: Any, R: Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3)->R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
  }

  internal interface SmsListener {
    fun onTextReceived(sms: SmsModel?)
  }
}