package com.ralphevmanzano.awssmsgateway.models

data class FcmResponse(val body: String, val title: String, val sms: Array<SmsEntity>) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FcmResponse

    if (body != other.body) return false
    if (title != other.title) return false
    if (!sms.contentEquals(other.sms)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = body.hashCode()
    result = 31 * result + title.hashCode()
    result = 31 * result + sms.contentHashCode()
    return result
  }
}