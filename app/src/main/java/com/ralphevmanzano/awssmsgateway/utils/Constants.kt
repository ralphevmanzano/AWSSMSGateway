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

// Other keys
const val API_WORKER_INPUT_KEY = "API_WORKER_INPUT_KEY"

// Preference keys
const val PREF_SERVER_IP = "PREF_SERVER_IP"

// Input Data Key
const val SMS_WORKER_INPUT_KEY = "SMS_WORKER_INPUT_KEY"