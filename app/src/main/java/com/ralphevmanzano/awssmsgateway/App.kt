package com.ralphevmanzano.awssmsgateway

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import com.facebook.stetho.Stetho

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Stetho.initializeWithDefaults(this)
  }
}