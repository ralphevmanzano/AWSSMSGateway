package com.ralphevmanzano.awssmsgateway.db

import androidx.room.*
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface SmsDao {

  @Query("SELECT * FROM messages")
  fun getMessages(): Flowable<List<SmsEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertMessages(messages: List<SmsEntity>): Completable

  @Delete
  fun delete(sms: SmsEntity)
}