package com.ralphevmanzano.awssmsgateway.models

import com.google.gson.annotations.SerializedName


data class ApiResponse(
  @SerializedName("message")
  val message: String,
  @SerializedName("status")
  val status: String,
  @SerializedName("userId")
  val userId: String
)