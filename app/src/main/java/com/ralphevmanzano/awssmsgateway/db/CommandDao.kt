package com.ralphevmanzano.awssmsgateway.db

import androidx.room.Dao
import androidx.room.Query
import com.ralphevmanzano.awssmsgateway.models.Command
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class CommandDao: BaseDao<Command>() {

  @Query("SELECT * FROM command")
  abstract fun getCommands(): Flowable<List<Command>>

  @Query("SELECT * FROM command WHERE id = :id")
  abstract fun getCommand(id: Int): Single<Command>



}