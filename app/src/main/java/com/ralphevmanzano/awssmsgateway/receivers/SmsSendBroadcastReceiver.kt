package com.ralphevmanzano.awssmsgateway.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ralphevmanzano.awssmsgateway.utils.DELIVERED_ACTION
import com.ralphevmanzano.awssmsgateway.utils.SENT_ACTION

class SmsSendBroadcastReceiver : BroadcastReceiver() {

  private var smsListener: SmsSentListener? = null

  override fun onReceive(context: Context?, intent: Intent?) {
    Log.d("SendBroadcast", "intent action: ${intent?.action}")
    when (intent?.action) {
      SENT_ACTION -> {
        when (resultCode) {
          Activity.RESULT_OK -> {
            Log.d("SendBroadcast", "Sent: OK")

            smsListener?.onSmsSent()
            /*context?.let {
              val dao = AwsDatabase.getInstance(it).smsDao()
              smsId?.let { id ->
                dao.delete(id)
                  .subscribeOn(Schedulers.io())
                  .subscribe({
                    Log.d("Room", "Successfully deleted entry $id!")
                    smsListener?.onSmsSent()
                  }, { error ->
                    Log.e("Room", "Error deleting $id: $error")
                    smsListener?.onSmsFailed()
                  })
              }
            }*/
          }
          else -> {
            Log.e("SendBroadcast", "Sent: FAILED $resultCode")
            smsListener?.onSmsFailed()
          }
        }
      }
      DELIVERED_ACTION -> {
        when (resultCode) {
          Activity.RESULT_OK -> {
            Log.d("SendBroadcast", "Delivered: OK")

            smsListener?.onSmsSent()
            /*context?.let {
              val dao = AwsDatabase.getInstance(it).smsDao()
              smsId?.let { id ->
                dao.delete(id)
                  .subscribeOn(Schedulers.io())
                  .subscribe({
                    Log.d("Room", "Successfully deleted entry $id!")
                    smsListener?.onSmsSent()
                  }, { error ->
                    Log.e("Room", "Error deleting $id: $error")
                    smsListener?.onSmsFailed()
                  })
              }
            }*/
          }
          else -> {
            Log.e("SendBroadcast", "Sent: FAILED $resultCode")
            smsListener?.onSmsFailed()
          }
        }
      }
    }

  }

  fun setListener(listener: SmsSentListener?) {
    listener?.let {
      smsListener = it
    }
  }

  interface SmsSentListener {
    fun onSmsSent()
    fun onSmsFailed()
  }
}