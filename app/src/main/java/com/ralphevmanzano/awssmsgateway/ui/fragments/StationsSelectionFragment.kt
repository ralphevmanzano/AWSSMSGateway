package com.ralphevmanzano.awssmsgateway.ui.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.adapters.StationsSelectionAdapter
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationSelectionSharedViewModel
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationsSelectionViewModel
import com.ralphevmanzano.awssmsgateway.utils.ItemDecoration
import com.ralphevmanzano.awssmsgateway.utils.StationDiffUtil
import kotlinx.android.synthetic.main.fragment_list.*

class StationsSelectionFragment : Fragment(), StationsSelectionAdapter.OnStationCheckedListener {

  private lateinit var adapter: StationsSelectionAdapter
  private lateinit var viewModel: StationsSelectionViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    txtTitle.text = getString(R.string.select_stations)
    btnAdd.text = getString(R.string.done)
    initRv()
    initListeners()
  }

  private fun initRv() {
    adapter = StationsSelectionAdapter(this, StationDiffUtil())
    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.divider_black)}
    drawable?.let { rv.addItemDecoration(ItemDecoration(it)) }

    rv.layoutManager = LinearLayoutManager(context)
    rv.adapter = adapter
  }

  private fun initListeners() {
    btnAdd.setOnClickListener {
      findNavController().popBackStack()
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(StationsSelectionViewModel::class.java)

    viewModel.getStations().observe(this, Observer {
      it?.let {
        adapter.submitList(it)
      }
    })

    viewModel.snackbarMessage.observe(this, Observer {
      it.getContentIfNotHandled()?.let { msg ->
        showSnackbar(msg)
      }
    })
  }

  override fun onStationChecked(isChecked: Boolean, station: Station) {
    viewModel.selectStation(isChecked, station)
  }

  private fun showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
  }
}

fun Station.print() {
  Log.d("Station", stationName)
}
