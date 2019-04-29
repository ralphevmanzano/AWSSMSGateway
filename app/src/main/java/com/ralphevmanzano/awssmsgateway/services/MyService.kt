package com.ralphevmanzano.awssmsgateway.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService: Service() {

    companion object {
        const val TAG = "MyService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created!")
        Thread.sleep(2000)
        Log.d(TAG, "Service stopping!")
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}