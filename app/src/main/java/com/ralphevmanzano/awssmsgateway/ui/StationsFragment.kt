package com.ralphevmanzano.awssmsgateway.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationsViewModel

class StationsFragment : Fragment() {

  companion object {
    fun newInstance() = StationsFragment()
  }

  private lateinit var viewModel: StationsViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.stations_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(StationsViewModel::class.java)
  }

}
