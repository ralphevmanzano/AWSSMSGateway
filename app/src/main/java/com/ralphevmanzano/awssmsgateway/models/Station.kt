package com.ralphevmanzano.awssmsgateway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class Station(
    @PrimaryKey
    var id: Int,
    val IMIE: Int,
    val mobileNo: String,
    val stationName: String,
    val description: String,
    val remarks: String
)