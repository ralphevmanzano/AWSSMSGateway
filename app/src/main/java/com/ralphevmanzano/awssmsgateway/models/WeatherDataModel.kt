package com.ralphevmanzano.awssmsgateway.models

data class WeatherDataModel(
  val stationId: String,
  val date: String,
  val time: String,
  val RSSI: String,
  val battVoltageLevel: String,
  val airTemperature: String,
  val relativeHumidity: String,
  val atmosphericPressure: String,
  val rainfall10m: String,
  val rainfall24h: String,
  val windDirection: String,
  val windSpeed: String,
  val solarIrridiance10m: String,
  val solarIrridiance1h: String,
  val checksum: String
)