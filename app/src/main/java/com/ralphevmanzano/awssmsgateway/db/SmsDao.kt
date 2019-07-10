package com.ralphevmanzano.awssmsgateway.db

import androidx.room.*
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class SmsDao : BaseDao<SmsEntity>() {

  @Query("SELECT * FROM messages")
  abstract fun getMessages(): Single<List<SmsEntity>>

  @Query("DELETE FROM messages WHERE id = :id")
  abstract fun delete(id: Int): Completable

  @Query("DELETE FROM messages")
  abstract fun deleteAll(): Completable
}