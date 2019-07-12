package com.ralphevmanzano.awssmsgateway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class Station(
    val stationName: String,
    val mobileNo: String,
    val imei: String,
    val description: String,
    val remarks: String
) : Comparable<Station> {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var isSelected: Boolean = false

    override fun compareTo(other: Station): Int {
        return stationName.compareTo(other.stationName)
    }
}