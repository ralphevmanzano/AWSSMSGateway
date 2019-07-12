package com.ralphevmanzano.awssmsgateway.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ralphevmanzano.awssmsgateway.db.AwsDatabase
import com.ralphevmanzano.awssmsgateway.db.StationDao
import com.ralphevmanzano.awssmsgateway.models.Station
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class StationsRepo(context: Context) {
  private val stationDao: StationDao = AwsDatabase.getInstance(context).stationDao()

  private val stationList = MutableLiveData<List<Station>>()

  fun addStation(station: Station): Completable {
    return stationDao.insert(station)
  }

  fun getStation(id: Int): Single<Station> {
    return stationDao.getStation(id)
  }

  fun getStations(): Flowable<List<Station>> {
    return stationDao.getStations()
  }

  fun getSelectedStations(): Flowable<List<Station>> {
    return stationDao.getSelectedStations()
  }

  fun updateStation(station: Station): Completable {
    return stationDao.update(station)
  }

  fun deleteStation(station: Station): Completable {
    return stationDao.delete(station)
  }
}