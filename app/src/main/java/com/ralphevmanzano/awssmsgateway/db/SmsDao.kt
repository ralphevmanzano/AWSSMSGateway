package com.ralphevmanzano.awssmsgateway.db

import androidx.room.*
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface SmsDao {

  @Query("SELECT * FROM messages")
  fun getMessages(): Single<List<SmsEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertMessages(messages: List<SmsEntity>): Completable

  @Query("DELETE FROM messages WHERE id = :id")
  fun delete(id: Int): Completable

  @Query("DELETE FROM messages")
  fun deleteAll(): Completable
}