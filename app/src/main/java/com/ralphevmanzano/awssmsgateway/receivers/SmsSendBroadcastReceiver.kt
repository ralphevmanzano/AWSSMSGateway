package com.ralphevmanzano.awssmsgateway.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ralphevmanzano.awssmsgateway.db.AwsDatabase
import com.ralphevmanzano.awssmsgateway.utils.SENT_ACTION
import com.ralphevmanzano.awssmsgateway.utils.SENT_INTENT_EXTRA
import io.reactivex.schedulers.Schedulers

class SmsSendBroadcastReceiver : BroadcastReceiver() {

  private var smsListener: SmsSentListener? = null

  override fun onReceive(context: Context?, intent: Intent?) {
    val smsId = intent?.getIntExtra(SENT_INTENT_EXTRA, 0)
    Log.d("SendBroadcast", "smsId from intent: $smsId")
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