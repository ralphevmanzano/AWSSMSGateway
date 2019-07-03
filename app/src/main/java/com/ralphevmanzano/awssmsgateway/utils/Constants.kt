@file:JvmName("Constants")
package com.ralphevmanzano.awssmsgateway.utils

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// Worker keys
const val API_WORKER_INPUT_KEY = "API_WORKER_INPUT_KEY"
const val SMS_UNIQUE_WORK = "SMS_UNIQUE_WORK"

// Preference keys
const val PREF_SERVER_IP = "PREF_SERVER_IP"

// EditDialogText Tag
const val DIALOG_SERVER_IP = "DIALOG_SERVER_IP"

// Input Data Key
const val SMS_WORKER_INPUT_KEY = "SMS_WORKER_INPUT_KEY"

// Intent Keys
const val SENT_INTENT_EXTRA = "SENT_INTENT_EXTRA"
const val DELIVERED_INTENT_EXTRA = "DELIVERED_INTENT_EXTRA"

// Intent Actions
const val SENT_ACTION = "com.ralphevmanzano.awssmsgateway.SMS_SEND"
const val DELIVERED_ACTION = "com.ralphevmazano.awssmsgateway.SMS_DELIVERED"
const val SMS_PROCESS_ACTION = "com.ralphevmanzano.awssmsgateway.SMS_PROCESS"
const val EXIT_APP_ACTION = "com.ralphevmanzano.awssmsgateway.EXIT_APP"
const val START_SERVICE = "com.ralphevmanzano.awssmsgateway.START_SERVICE"