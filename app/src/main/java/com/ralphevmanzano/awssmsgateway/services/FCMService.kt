package com.ralphevmanzano.awssmsgateway.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.ui.MainActivity
import com.ralphevmanzano.awssmsgateway.db.SmsDatabase
import com.ralphevmanzano.awssmsgateway.models.FcmResponse
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.utils.SMS_PROCESS_ACTION
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*


class FCMService : FirebaseMessagingService() {

  private var notificationManager: NotificationManager? = null
  private val disposable = CompositeDisposable()
  private var fcmResponse: FcmResponse? = null
  private var messages: Queue<SmsEntity> = ArrayDeque()

  companion object {
    const val DEFAULT_CHANNEL_NAME = "FCM Notifications"
    const val DEFAULT_CHANNEL_ID = "fcm_notification_id"
  }

  override fun onCreate() {
    super.onCreate()
//    smsSendBroadcastReceiver = SmsSendBroadcastReceiver()
//    smsSendBroadcastReceiver?.setListener(this)
//    applicationContext.registerReceiver(smsSendBroadcastReceiver, IntentFilter(SENT_ACTION))
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.clear()
//    smsSendBroadcastReceiver?.setListener(null)
//    applicationContext.unregisterReceiver(smsSendBroadcastReceiver)
  }

  override fun onMessageReceived(msg: RemoteMessage?) {
    super.onMessageReceived(msg)
    // Check if message contains a data payload.

    msg?.data?.isNotEmpty()?.let {
      val jsonStr = JSONObject(msg.data).toString()

      val finalJson = jsonStr.replace("\\\\", "")
        .replace("\"[", "[")
        .replace("]\"", "]")
        .replace("\\\"", "\"")

      Log.d("Sms", "new JSONObject(remoteMessage.getData()) : $finalJson")

      fcmResponse = Gson().fromJson(finalJson, FcmResponse::class.java)

      fcmResponse?.let {
//        showNotification(it.title, it.body)
        for (i in it.sms) {
          messages.add(i)
        }
        saveMessagesToDb(it.sms)
      }
    }
  }

  private fun saveMessagesToDb(sms: Array<SmsEntity>) {
    val dao = SmsDatabase.getInstance(applicationContext).smsDao()
    disposable.add(dao.insertMessages(sms.toList())
      .subscribeOn(Schedulers.io())
      .subscribe({
        Log.d("Room", "Successfully inserted!")
        broadcastSmsProcess()
      }, { error ->
        Log.e("Room", "Error inserting messages: $error")
      }))
  }

  private fun broadcastSmsProcess() {
    Log.d("Sms", "Broadcasting process intent")
    val intent = Intent()
    intent.action = SMS_PROCESS_ACTION
    sendBroadcast(intent)
  }

  private fun showNotification(title: String?, msg: String?) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(
      this, 0 /* Request code */, intent,
      PendingIntent.FLAG_ONE_SHOT
    )

    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel()

    val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
      .setContentTitle(title)
      .setContentText(msg)
      .setSmallIcon(android.R.drawable.sym_def_app_icon)
      .setContentIntent(pendingIntent)
      .build()

    //Send the notification.
    notificationManager!!.notify(10, notification)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      //Create channel only if it is not already created
      if (notificationManager?.getNotificationChannel(DEFAULT_CHANNEL_ID) == null) {
        notificationManager?.createNotificationChannel(
          NotificationChannel(
            DEFAULT_CHANNEL_ID,
            DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
          )
        )
      }
    }
  }
}