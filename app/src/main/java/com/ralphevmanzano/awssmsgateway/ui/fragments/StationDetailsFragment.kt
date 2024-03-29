package com.ralphevmanzano.awssmsgateway.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.FragmentStationDetailsBinding
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_station_details.*

class StationDetailsFragment : Fragment() {

  private val args: StationDetailsFragmentArgs by navArgs()
  private lateinit var binding: FragmentStationDetailsBinding
  private lateinit var viewModel: StationDetailsViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_station_details, container,false)
    return binding.root
  }

  override fun onStart() {
    super.onStart()
    activity?.toolbar?.let {
      when(args.type) {
        StationsFragment.STATIONS_VIEW-> it.title = getString(R.string.view_station)
        StationsFragment.STATIONS_EDIT -> it.title = getString(R.string.edit_station)
        StationsFragment.STATIONS_ADD -> it.title = getString(R.string.add_new_station)
      }
    }

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initListeners()
  }

  private fun initListeners() {
    btnSave.setOnClickListener {
      if (args.type == StationsFragment.STATIONS_ADD) viewModel.addStation()
      else if (args.type == StationsFragment.STATIONS_EDIT) viewModel.updateStation()
    }
    btnCancel.setOnClickListener { findNavController().popBackStack() }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(StationDetailsViewModel::class.java)

    binding.viewModel = viewModel
    binding.lifecycleOwner = viewLifecycleOwner

    viewModel.setAction(args.type)

    viewModel.actionType.observe(this, Observer {
      when(it) {
        StationsFragment.STATIONS_VIEW -> {
          viewModel.title.value = getString(R.string.view)
          viewModel.setAreFieldsEditable(false)
          viewModel.getStation(args.id)
        }
        StationsFragment.STATIONS_EDIT -> {
          viewModel.title.value = getString(R.string.edit)
          viewModel.setAreFieldsEditable(true)
          viewModel.getStation(args.id)
        }
        StationsFragment.STATIONS_ADD -> {
          viewModel.title.value = getString(R.string.add_new)
          viewModel.setAreFieldsEditable(true)
        }
      }
    })

    viewModel.snackbarMessage.observe(this, Observer {
      it.getContentIfNotHandled()?.let { msg ->
        showSnackbar(msg)
      }
    })
  }

  private fun showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
  }

}
