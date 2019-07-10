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

  fun deleteStation(station: Station): Completable {
    return stationDao.delete(station)
  }

//  fun getStations() : LiveData<List<Station>> {
//    val stations = ArrayList<Station>()
//    stations.add(Station(12312313, "09179536401", "Ralph station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(322332233, "09179536401", "Emerson station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(222222222, "09179536401", "Jada station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(434343434, "09179536401", "Larp station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(213132232, "09179536401", "Anne station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(434314223, "09179536401", "Manzano station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(2131313131, "09179536401", "Anne station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(111111111, "09179536401", "Manne station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(1121212112, "09179536401", "Jel station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(1232321, "09179536401", "Aaron station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(213213313, "09179536401", "Earl station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(133434334, "09179536401", "Ruby station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(23423424, "09179536401", "Ira station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(322323232, "09179536401", "Sean station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stations.add(Station(2323232, "09179536401", "Franz station", "Kay ralph ni didto sa dabaw", "Nice"))
//    stationList.value = stations
//
//    return stationList
//  }
}