package com.ralphevmanzano.awssmsgateway.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ralphevmanzano.awssmsgateway.models.SmsEntity

@Database(entities = arrayOf(SmsEntity::class), version = 1)
abstract class SmsDatabase: RoomDatabase() {

  abstract fun smsDao(): SmsDao

  companion object {
    @Volatile private var INSTANCE: SmsDatabase? = null

    fun getInstance(context: Context): SmsDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
      }

    private fun buildDatabase(context: Context) =
      Room.databaseBuilder(context.applicationContext,
        SmsDatabase::class.java, "Sample.db")
        .build()
  }
}