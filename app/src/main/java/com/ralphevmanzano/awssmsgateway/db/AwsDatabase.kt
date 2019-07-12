package com.ralphevmanzano.awssmsgateway.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ralphevmanzano.awssmsgateway.models.Command
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.models.Station

@Database(entities = [SmsEntity::class, Station::class, Command::class], version = 1)
abstract class AwsDatabase: RoomDatabase() {

  abstract fun smsDao(): SmsDao

  abstract fun stationDao(): StationDao

  abstract fun commandDao(): CommandDao

  companion object {
    @Volatile private var INSTANCE: AwsDatabase? = null

    fun getInstance(context: Context): AwsDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
      }

    private fun buildDatabase(context: Context) =
      Room.databaseBuilder(context.applicationContext,
        AwsDatabase::class.java, "awssms.db")
        .build()
  }
}