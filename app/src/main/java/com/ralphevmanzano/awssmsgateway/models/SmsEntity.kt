package com.ralphevmanzano.awssmsgateway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class SmsEntity(
  val num: String,
  val message: String
) {
  @PrimaryKey(autoGenerate = true)
  var smsId: Int = 0
}