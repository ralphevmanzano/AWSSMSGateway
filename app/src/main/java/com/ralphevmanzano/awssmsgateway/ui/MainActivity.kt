package com.ralphevmanzano.awssmsgateway.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.SmsModel
import com.ralphevmanzano.awssmsgateway.receivers.SmsReceiveBroadcastReceiver
import com.ralphevmanzano.awssmsgateway.services.AwsService
import com.ralphevmanzano.awssmsgateway.utils.EXIT_APP_ACTION
import com.ralphevmanzano.awssmsgateway.utils.START_SERVICE
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

  private lateinit var rxPermissions: RxPermissions
  private lateinit var disposable: Disposable
  private lateinit var smsReceiveBroadcastReceiver: SmsReceiveBroadcastReceiver
  private lateinit var appBarConfig: AppBarConfiguration

  private val exitBroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val stopServiceIntent = Intent(this@MainActivity, AwsService::class.java)
      stopService(stopServiceIntent)
      finish()
    }
  }

  companion object {
    const val TAG = "MainActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    rxPermissions = RxPermissions(this)
    rxPermissions.setLogging(true)
    smsReceiveBroadcastReceiver = SmsReceiveBroadcastReceiver()
    registerReceiver(exitBroadcastReceiver, IntentFilter(EXIT_APP_ACTION))

    val host = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    val navController = host.navController
    appBarConfig = AppBarConfiguration(navController.graph)

    setSupportActionBar(toolbar)
    setupActionBarWithNavController(navController, appBarConfig)

    requestReadAndSendSmsPermission()
    initFCM()
    initAwsService()
  }

  override fun onSupportNavigateUp(): Boolean {
    return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfig)
  }

  private fun initAwsService() {
    val serviceIntent = Intent(this, AwsService::class.java)
    serviceIntent.action = START_SERVICE
    ContextCompat.startForegroundService(this, serviceIntent)
  }

  private fun initFCM() {
    FirebaseMessaging.getInstance().subscribeToTopic("aws")
      .addOnCompleteListener { task ->
        var msg = "Subscribed to Firebase Topic: AWS"
        if (!task.isSuccessful)
          msg = "Failed to subscribe"
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
      }
  }

  override fun onDestroy() {
    unregisterReceiver(exitBroadcastReceiver)
    smsReceiveBroadcastReceiver.setListener(null)
    super.onDestroy()
  }

  /**
   * Request runtime SmsModel permission
   */
  private fun requestReadAndSendSmsPermission() {
    disposable = rxPermissions
      .requestEachCombined(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE
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
    smsReceiveBroadcastReceiver.setListener(object : SmsReceiveBroadcastReceiver.SmsListener {
      override fun onTextReceived(sms: SmsModel?) {
        Log.d(TAG, sms.toString())
      }
    })
  }


  private fun showPermissionExplanationDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(getString(R.string.permission_explanation))
    builder.setPositiveButton(getString(R.string.settings)) { _, _ ->
      goToSettings()
    }
    builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
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
