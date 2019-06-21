package com.ralphevmanzano.awssmsgateway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class SmsEntity(
  @PrimaryKey
  var id: Int,
  val num: String,
  val message: String
)