package com.ralphevmanzano.awssmsgateway

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.ralphevmanzano.awssmsgateway.models.SMS
import com.ralphevmanzano.awssmsgateway.utils.PREF_SERVER_IP
import com.ralphevmanzano.awssmsgateway.utils.PreferenceHelper
import com.ralphevmanzano.awssmsgateway.utils.PreferenceHelper.set
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable


class MainActivity : AppCompatActivity() {

  private lateinit var rxPermissions: RxPermissions
  private lateinit var disposable: Disposable
  private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

  companion object {
    val TAG = "MainActivity";
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    rxPermissions = RxPermissions(this)
    rxPermissions.setLogging(true)
    smsBroadcastReceiver = SmsBroadcastReceiver()

    val pref = PreferenceHelper.defaultPrefs(this)
    if (pref.getString(PREF_SERVER_IP, null) == null) {
      pref[PREF_SERVER_IP] = "http://192.168.1.19/"
    }

    requestReadAndSendSmsPermission()
    initFCM()
  }

  private fun initFCM() {
    FirebaseInstanceId.getInstance().instanceId
      .addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
          Log.w(TAG, "getInstanceId failed", task.exception)
          return@OnCompleteListener
        }

        // Get new Instance ID token
        val token = task.result?.token

        // Log and toast
        val msg = getString(R.string.msg_token_fmt, token)
        Log.d(TAG, msg)
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
      })
  }

  override fun onDestroy() {
    super.onDestroy()
    smsBroadcastReceiver.setListener(null)
  }

  /**
   * Request runtime SMS permission
   */
  private fun requestReadAndSendSmsPermission() {
    disposable = rxPermissions
      .requestEachCombined(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS
      )
      .subscribe { perm ->
        when {
          perm.granted -> setBroadcastReceiverListener()
          perm.shouldShowRequestPermissionRationale -> showPermissionExplanationDialog()
          else -> showPermissionExplanationDialog()
        }
      }
  }

  private fun setBroadcastReceiverListener() {
    smsBroadcastReceiver.setListener(object : SmsBroadcastReceiver.SmsListener {
      override fun onTextReceived(sms: SMS) {
        Log.d("MainActivity", sms.toString())
      }
    })
  }


  private fun showPermissionExplanationDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("These permissions are required to make use of this application. Please manually grant the permission in the settings.")
    builder.setPositiveButton("Settings") { _, _ ->
      goToSettings()
    }
    builder.setNegativeButton("Cancel") { _, _ ->
      finish()
    }

    val dialog: AlertDialog = builder.create()
    dialog.show()
  }

  private fun goToSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
  }

}
