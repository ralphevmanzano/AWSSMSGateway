package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ralphevmanzano.awssmsgateway.models.Station

class StationSelectionSharedViewModel: ViewModel() {
  private val _selectedStations = MutableLiveData<ArrayList<Station>>()
  private val stationsList = ArrayList<Station>()

  val selectedStations: LiveData<ArrayList<Station>>
    get() = _selectedStations

  fun stationClicked(isChecked: Boolean, station: Station) {
    if (isChecked) {
      stationsList.add(station)
      _selectedStations.postValue(stationsList)
    } else {
      stationsList.remove(station)
      _selectedStations.postValue(stationsList)
    }
  }
}