package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.ralphevmanzano.awssmsgateway.data.StationsRepo
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StationsSelectionViewModel(application: Application) : AndroidViewModel(application) {
  private val stationsRepo: StationsRepo = StationsRepo(application)
  private val disposable = CompositeDisposable()

  private val _stations = MutableLiveData<List<Station>>()
  private val _snackbarMessage = MutableLiveData<Event<String>>()

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  fun getStations(): LiveData<List<Station>> {
    disposable.add(stationsRepo.getStations()
      .subscribeOn(Schedulers.io())
      .subscribe({ stations ->
        _stations.postValue(stations)
      }, { error ->
        _snackbarMessage.postValue(Event("Error fetching stations"))
        Log.e("StationsViewModel", "Error fetching stations: ${error.localizedMessage}")
      })
    )

    return _stations
  }

  fun selectStation(isChecked: Boolean, station: Station) {
    station.isSelected = isChecked

    disposable.add(stationsRepo.updateStation(station)
      .subscribeOn(Schedulers.io())
      .subscribe({},{
        _snackbarMessage.postValue(Event("An unexpected error occurred"))
        Log.e("StationsSelectionVM", "Error selecting station ${it.localizedMessage}")
      }))
  }

  override fun onCleared() {
    disposable.clear()
  }
}
