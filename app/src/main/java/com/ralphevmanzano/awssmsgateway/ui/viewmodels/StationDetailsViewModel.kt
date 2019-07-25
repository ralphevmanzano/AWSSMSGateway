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

class StationDetailsViewModel(application: Application) : AndroidViewModel(application), Observable {
  private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.add(callback)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.remove(callback)
  }

  private val stationsRepo: StationsRepo = StationsRepo(application)
  private val disposable = CompositeDisposable()
  private val _snackbarMessage = MutableLiveData<Event<String>>()
  private val _actionType = MutableLiveData<Int>()
  private val _areFieldsEditable = MutableLiveData<Boolean>()

  private var stationId = -1
  val title = MutableLiveData<String>()
  val stationName = MutableLiveData<String>()
  val mobileNo = MutableLiveData<String>()
  val imei = MutableLiveData<String>()
  val description = MutableLiveData<String>()
  val remarks = MutableLiveData<String>()

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  val actionType: LiveData<Int>
    get() = _actionType

  val areFieldsEditable: LiveData<Boolean>
    get() = _areFieldsEditable

  fun setAction(type: Int) {
    _actionType.value = type
  }

  fun setAreFieldsEditable(isEditable: Boolean) {
    _areFieldsEditable.value = isEditable
  }

  private fun isVerified(): Boolean {
    return !(stationName.value.isNullOrEmpty() ||
        mobileNo.value.isNullOrEmpty() ||
        imei.value.isNullOrEmpty() ||
        description.value.isNullOrEmpty() ||
        remarks.value.isNullOrEmpty())
  }

  fun addStation() {
    if (!isVerified()) {
      _snackbarMessage.postValue(Event("Please fill up all the fields"))
      return
    }

    val station = Station(
      stationName.value!!,
      mobileNo.value!!,
      imei.value!!,
      description.value!!,
      remarks.value!!
    )

    disposable.add(stationsRepo.addStation(station)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Station added successfully!"))
        stationName.postValue("")
        mobileNo.postValue("")
        imei.postValue("")
        description.postValue("")
        remarks.postValue("")
      }, { error ->
        _snackbarMessage.postValue(Event("Error adding new station"))
        Log.e("StationsViewModel", "Error adding new station ${error.localizedMessage}")
      })
    )
  }

  fun updateStation() {
    if (!isVerified()) {
      _snackbarMessage.postValue(Event("Please fill up all the fields"))
      return
    }

    val station = Station(
      stationName.value!!,
      mobileNo.value!!,
      imei.value!!,
      description.value!!,
      remarks.value!!
    )

    if (stationId != -1) {
      station.id = stationId
    }

    disposable.add(stationsRepo.updateStation(station)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Station updated successfully!"))
      }, { error ->
        _snackbarMessage.postValue(Event("Error updating new station"))
        Log.e("StationsViewModel", "Error updating new station ${error.localizedMessage}")
      })
    )
  }

  fun getStation(id: Int) {
    disposable.add(stationsRepo.getStation(id)
      .subscribeOn(Schedulers.io())
      .subscribe({
        stationId = it.id
        stationName.postValue(it.stationName)
        mobileNo.postValue(it.mobileNo)
        imei.postValue(it.imei)
        description.postValue(it.description)
        remarks.postValue(it.remarks)
      }, {
        _snackbarMessage.postValue(Event("Error getting station: ${it.localizedMessage}"))
        Log.e("StationsViewModel", "Error getting station: ${it.localizedMessage}")
      })
    )
  }


}
