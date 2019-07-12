package com.ralphevmanzano.awssmsgateway.ui.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip

import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.FragmentSendCommandBinding
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.SendCommandViewModel
import kotlinx.android.synthetic.main.fragment_send_command.*
import java.util.*

class SendCommandFragment : Fragment() {

  private val args: SendCommandFragmentArgs by navArgs()
  private lateinit var binding: FragmentSendCommandBinding
  private lateinit var viewModel: SendCommandViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_command, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initListeners()
  }

  private fun initListeners() {
    fl.setOnClickListener {
      viewModel.onNavigateToSelectionClick(listOf())
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(SendCommandViewModel::class.java)

    binding.viewModel = viewModel
    binding.lifecycleOwner = viewLifecycleOwner

    viewModel.command.value = args.command

    viewModel.navigateToSelection.observe(this, Observer {
      it.getContentIfNotHandled()?.let {
        findNavController().navigate(R.id.actionStationsSelection)
      }
    })

    viewModel.getSelectedStations().observe(this, Observer {
      txtChipInstructions.visibility = View.VISIBLE
      it?.let {
        if (it.isEmpty()) {
          cg.removeAllViews()
          return@let
        }

        txtChipInstructions.visibility = View.INVISIBLE

        for (s in it) {
          cg.removeAllViews()
          addChips(it)
        }
      }
    })

  }


  private fun addChips(stations: List<Station>) {
    for (s in stations) {
      val chip = Chip(context)

      chip.text = s.stationName
      chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_station)
      chip.isCloseIconVisible = true

      // necessary to get single selection working
      chip.isClickable = true
      chip.isCheckable = false

      cg.addView(chip as View)

      chip.setOnCloseIconClickListener {
        viewModel.deselectStation(s)
      }
    }
  }

}
