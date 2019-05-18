package com.ralphevmanzano.awssmsgateway.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.MainActivity
import com.ralphevmanzano.awssmsgateway.models.FcmResponse
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import org.json.JSONObject



class FCMService : FirebaseMessagingService() {

  private var notificationManager: NotificationManager? = null

  companion object {
    const val DEFAULT_CHANNEL_NAME = "FCM Notifications"
    const val DEFAULT_CHANNEL_ID = "fcm_notification_id"
  }

  override fun onMessageReceived(msg: RemoteMessage?) {
    super.onMessageReceived(msg)
    // Check if message contains a data payload.

    msg?.data?.isNotEmpty()?.let {
      val jsonStr = JSONObject(msg.data).toString()
      Log.d("Sms", "new JSONObject(remoteMessage.getData()) : $jsonStr")

      val finalJson = jsonStr.replace("\\\\", "")
        .replace("\"[", "[")
        .replace("]\"", "]")
        .replace("\\\"", "\"")

      Log.d("Sms", "new JSONObject(remoteMessage.getData()) : $finalJson")


      val fcmResponse = Gson().fromJson(finalJson, FcmResponse::class.java)

      showNotification(fcmResponse.title, fcmResponse.body)
      for (sms in fcmResponse.sms) {
        Log.d("Sms", "Sms ${sms.num} ${sms.message}")
      }
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
    }
  }

  /**
   * Called if InstanceID token is updated. This may occur if the security of
   * the previous token had been compromised. Note that this is called when the InstanceID token
   * is initially generated so this is where you would retrieve the token.
   */
  override fun onNewToken(p0: String?) {
    super.onNewToken(p0)
    // call to backend
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