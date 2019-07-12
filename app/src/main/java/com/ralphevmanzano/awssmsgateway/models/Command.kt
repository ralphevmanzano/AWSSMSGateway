package com.ralphevmanzano.awssmsgateway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command")
data class Command(
  val commandName: String,
  val description: String,
  val remarks: String
) {
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
}