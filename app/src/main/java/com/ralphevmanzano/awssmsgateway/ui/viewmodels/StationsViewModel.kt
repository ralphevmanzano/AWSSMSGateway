package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import com.ralphevmanzano.awssmsgateway.models.Station

class StationsViewModel : ViewModel() {
  fun getStations(): LiveData<List<Station>> {

  }
}
