package com.ralphevmanzano.awssmsgateway.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.FragmentStationDetailsBinding
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationDetailsViewModel
import kotlinx.android.synthetic.main.fragment_station_details.*

class StationDetailsFragment : Fragment() {

  private val args: StationDetailsFragmentArgs by navArgs()
  private lateinit var binding: FragmentStationDetailsBinding

  companion object {
    fun newInstance() = StationDetailsFragment()
  }

  private lateinit var viewModel: StationDetailsViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_station_details, container,false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initListeners()
  }

  private fun initListeners() {
    btnSave.setOnClickListener { viewModel.addStation() }

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
        StationsFragment.STATION_VIEW -> {
          viewModel.title.value = getString(R.string.view)
          viewModel._areFieldsEditable.value = false
          viewModel.getStation(args.id)
        }
        StationsFragment.STATION_EDIT -> {
          viewModel.title.value = getString(R.string.edit)
          viewModel._areFieldsEditable.value = true
          viewModel.getStation(args.id)
        }
        StationsFragment.STATION_ADD -> {
          viewModel.title.value = getString(R.string.add_new)
          viewModel._areFieldsEditable.value = true
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
