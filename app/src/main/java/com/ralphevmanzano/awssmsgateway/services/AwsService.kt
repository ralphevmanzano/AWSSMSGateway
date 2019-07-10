package com.ralphevmanzano.awssmsgateway.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.db.AwsDatabase
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.receivers.SmsSendBroadcastReceiver
import com.ralphevmanzano.awssmsgateway.ui.MainActivity
import com.ralphevmanzano.awssmsgateway.utils.*
import com.ralphevmanzano.awssmsgateway.workers.SmsWorker
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class AwsService: Service(), SmsSendBroadcastReceiver.SmsSentListener {

  private var notificationManager: NotificationManager? = null
  private val disposable = CompositeDisposable()
  private var smsSendBroadcastReceiver: SmsSendBroadcastReceiver? = null
  private var messages: Queue<SmsEntity> = ArrayDeque()
  private var pendingSmsDisposable: Disposable? = null

  private val smsProcessReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      Log.d("AwsService", "Broadcast process intent received!")
      checkPendingSms()
    }
  }

  companion object {
    const val AWS_CHANNEL_NAME = "Sms Sent Status Service"
    const val AWS_CHANNEL_ID = "aws_service_channel_id"
  }

  override fun onCreate() {
    super.onCreate()
    smsSendBroadcastReceiver = SmsSendBroadcastReceiver()
    registerReceiver(smsSendBroadcastReceiver, IntentFilter(SENT_ACTION))
    smsSendBroadcastReceiver?.setListener(this)
    registerReceiver(smsProcessReceiver, IntentFilter(SMS_PROCESS_ACTION))
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    pendingSmsDisposable = Observable.interval(10, 60, TimeUnit.SECONDS)
      .subscribe {
        Log.d("PendingSms", "***********Monitoring Pendin SMS*************")
        checkPendingSms()
      }
  }

  override fun onDestroy() {
    smsSendBroadcastReceiver?.setListener(null)
    unregisterReceiver(smsSendBroadcastReceiver)
    unregisterReceiver(smsProcessReceiver)
    pendingSmsDisposable?.dispose()
    super.onDestroy()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.let {
      when(it.action) {
        START_SERVICE -> showNotification()
        EXIT_APP_ACTION -> exitApplication()
      }
    }

    return START_STICKY
  }

  private fun exitApplication() {
    val exitIntent = Intent(EXIT_APP_ACTION)
    sendBroadcast(exitIntent)
  }

  private fun showNotification() {
    val notificationIntent = Intent(this, MainActivity::class.java)
    val openActivityPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

    val exitAppIntent = Intent(this, AwsService::class.java)
    exitAppIntent.action = EXIT_APP_ACTION
    val exitAppPendingIntent = PendingIntent.getService(this, 0, exitAppIntent, 0)
    createNotificationChannel()

    val notification = NotificationCompat.Builder(this, AWS_CHANNEL_ID)
      .setContentTitle(getString(R.string.aws_service_title))
      .setContentText("Aws Sms Gateway is running")
      .setSmallIcon(android.R.drawable.sym_def_app_icon)
      .addAction(R.drawable.ic_exit, getString(R.string.exit), exitAppPendingIntent)
      .setContentIntent(openActivityPendingIntent)
      .build()

    startForeground(1, notification)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun checkPendingSms() {
    Log.d("PendingSms", "Checking Database SMS")

    val dao = AwsDatabase.getInstance(applicationContext).smsDao()
    disposable.add(dao.getMessages()
      .subscribeOn(Schedulers.io())
      .subscribe({ sms ->
        sms?.let {
          Log.d("Sms", "Sms Size = ${sms.size}")
          messages.clear()
          for (s in sms) {
            Log.d("Sms", "Sms Size = ${sms.size}")
            messages.add(s)
          }
          processSms()
        }
      }, { error ->
        Log.e("PendingSms", "Failed fetching entries of database ${error.localizedMessage}")
      }))
  }

  private fun processSms() {
    Log.d("PendingSms", "Processing SMS")
    if (messages.isNotEmpty()) {
      messages.remove()?.let { startSmsWork(it) }
    }
  }

  private fun startSmsWork(sms: SmsEntity) {
    val gson = Gson()

    val smsWorkerRequestBuilder = OneTimeWorkRequestBuilder<SmsWorker>()
    smsWorkerRequestBuilder.setInputData(workDataOf(SMS_WORKER_INPUT_KEY to gson.toJson(sms)))

    WorkManager.getInstance().enqueueUniqueWork(SMS_UNIQUE_WORK, ExistingWorkPolicy.APPEND, smsWorkerRequestBuilder.build())
  }

  override fun onSmsSent() {
    processSms()
  }

  override fun onSmsFailed() {
    Log.d("Sms", "Failed sending sms: clearing stack")
    messages.clear()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      //Create channel only if it is not already created
      if (notificationManager?.getNotificationChannel(AWS_CHANNEL_ID) == null) {
        notificationManager?.createNotificationChannel(
          NotificationChannel(
            AWS_CHANNEL_ID,
            AWS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
          )
        )
      }
    }
  }
}