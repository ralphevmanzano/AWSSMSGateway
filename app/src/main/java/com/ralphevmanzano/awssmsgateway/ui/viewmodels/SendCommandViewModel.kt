package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ralphevmanzano.awssmsgateway.data.StationsRepo
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SendCommandViewModel(application: Application) : AndroidViewModel(application), Observable {

  private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.add(callback)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.remove(callback)
  }

  private val stationsRepo: StationsRepo = StationsRepo(application)
  private val disposable = CompositeDisposable()
  private val _navigateToSelection = MutableLiveData<Event<List<Station>>>()
  private val _sendCommand = MutableLiveData<Event<String>>()
  private val _selectedStations = MutableLiveData<List<Station>>()
  private val _snackbarMessage = MutableLiveData<Event<String>>()

  val command = MutableLiveData<String>()

  val navigateToSelection: LiveData<Event<List<Station>>>
    get() = _navigateToSelection

  val sendCommand: LiveData<Event<String>>
    get() = _sendCommand

  fun onNavigateToSelectionClick(stations: List<Station>) {
    _navigateToSelection.value = Event(stations)
  }

  fun onSendCommandClick(s: String) {
    _sendCommand.value = Event(s)
  }

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  fun setSnackbarMessage(s: String) {
    _snackbarMessage.postValue(Event(s))
  }

  fun getSelectedStations(): LiveData<List<Station>> {
    disposable.add(stationsRepo.getSelectedStations()
      .subscribeOn(Schedulers.io())
      .subscribe({
        _selectedStations.postValue(it)
      }, {
        _snackbarMessage.postValue(Event("An unexpected error occurred"))
        Log.e("SendCommandVM", "Error getting selected stations ${it.localizedMessage}")
      }))

    return _selectedStations
  }

  fun deselectStation(station: Station) {
    station.isSelected = false

    disposable.add(stationsRepo.updateStation(station)
      .subscribeOn(Schedulers.io())
      .subscribe({},{
        _snackbarMessage.postValue(Event("An unexpected error occurred"))
        Log.e("SendCommandVM", "Error getting selected stations ${it.localizedMessage}")
      }))
  }

  override fun onCleared() {
    disposable.clear()
  }
}
