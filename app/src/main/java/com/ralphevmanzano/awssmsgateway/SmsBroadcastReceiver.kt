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


class SmsBroadcastReceiver :
    BroadcastReceiver() {
    private val TAG = "SmsBroadcastReceiver"

    private var smsListener: SmsListener? = null
    private var notificationManager: NotificationManager? = null



    companion object {
        const val DEFAULT_CHANNEL_NAME = "SMS Notifications"
        const val DEFAULT_CHANNEL_ID = "sms_notification_id"
    }

    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender: String? = null
            var smsBody = ""
            var smsTimestamp: Long = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.displayOriginatingAddress
                    smsBody += smsMessage.messageBody
                    smsTimestamp = smsMessage.timestampMillis
                    Log.d("Receiver", "SMS Received!! \n $smsMessage")
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
                    smsSender = messages[0]!!.originatingAddress
                    smsTimestamp = messages[0]!!.timestampMillis
                }
            }

            if (smsListener != null) {
                Log.d("Receiver", smsBody)
                smsListener!!.onTextReceived(SMS(smsSender!!, smsBody, smsTimestamp))
            }

            startService()
            initNotification()
        }
    }

    private fun startService() {
        val i = Intent(context, MyService::class.java)
        context.startService(i)
    }

    internal fun setListener(smsListener: SmsListener?) {
        Log.d("Receiver", "Set listener")
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

    internal interface SmsListener {
        fun onTextReceived(sms: SMS)
    }
}