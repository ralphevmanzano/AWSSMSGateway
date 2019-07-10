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

class StationsViewModel(application: Application) : AndroidViewModel(application) {
  private val stationsRepo: StationsRepo = StationsRepo(application)
  private val disposable = CompositeDisposable()

  private val _openMoreDialog = MutableLiveData<Event<Int>>()
  private val _navigateToEdit = MutableLiveData<Event<Int>>()
  private val _selectedItem = MutableLiveData<Int>()
  private val _snackbarMessage = MutableLiveData<Event<String>>()
  private val _stations = MutableLiveData<List<Station>>()

  val openMoreDialog: LiveData<Event<Int>>
    get() = _openMoreDialog

  val navigateToEdit: LiveData<Event<Int>>
    get() = _navigateToEdit

  val selectedItem: LiveData<Int>
    get() = _selectedItem

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  fun selectItem(id: Int) {
    _selectedItem.value = id
  }

  fun onEditClick(id: Int) {
    _navigateToEdit.value = Event(id)
  }

  fun onOpenDialogClick(itemId: Int) {
    _openMoreDialog.value = Event(itemId)  // Trigger the event by setting a new Event as a new value
  }

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

  fun deleteStation(station: Station) {
    disposable.add(stationsRepo.deleteStation(station)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Successfully deleted ${station.stationName}!"))
      }, { error ->
        _snackbarMessage.postValue(Event("Error deleting ${station.stationName}"))
        Log.e("StationsViewModel", "Error deleting station: ${error.localizedMessage}")
      }))
  }

  override fun onCleared() {
    disposable.clear()
  }
}
