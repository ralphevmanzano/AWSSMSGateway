package com.ralphevmanzano.awssmsgateway.db

import androidx.room.Dao
import androidx.room.Query
import com.ralphevmanzano.awssmsgateway.models.Station
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class StationDao: BaseDao<Station>() {

  @Query("SELECT * FROM station")
  abstract fun getStations(): Flowable<List<Station>>

  @Query("SELECT * FROM station WHERE id = :id")
  abstract fun getStation(id: Int): Single<Station>

}