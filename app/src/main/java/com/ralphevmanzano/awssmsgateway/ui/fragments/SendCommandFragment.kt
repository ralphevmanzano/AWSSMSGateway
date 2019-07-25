package com.ralphevmanzano.awssmsgateway.ui.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.FragmentSendCommandBinding
import com.ralphevmanzano.awssmsgateway.models.SmsEntity
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.receivers.SmsSendBroadcastReceiver
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.SendCommandViewModel
import com.ralphevmanzano.awssmsgateway.utils.DELIVERED_ACTION
import com.ralphevmanzano.awssmsgateway.utils.SENT_ACTION
import com.ralphevmanzano.awssmsgateway.utils.SMS_UNIQUE_WORK
import com.ralphevmanzano.awssmsgateway.utils.SMS_WORKER_INPUT_KEY
import com.ralphevmanzano.awssmsgateway.workers.SmsWorker
import kotlinx.android.synthetic.main.fragment_send_command.*
import java.util.*

class SendCommandFragment : Fragment(), SmsSendBroadcastReceiver.SmsSentListener {

  private val args: SendCommandFragmentArgs by navArgs()
  private lateinit var binding: FragmentSendCommandBinding
  private lateinit var viewModel: SendCommandViewModel
  private val stationsQueue: Queue<Station> = ArrayDeque()
  private val messagesQueue: Queue<SmsEntity> = ArrayDeque()
  private var smsSendBroadcastReceiver: SmsSendBroadcastReceiver? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    smsSendBroadcastReceiver = SmsSendBroadcastReceiver()
    requireActivity().registerReceiver(smsSendBroadcastReceiver, IntentFilter(SENT_ACTION))
    smsSendBroadcastReceiver?.setListener(this)
  }

  override fun onDestroy() {
    smsSendBroadcastReceiver?.setListener(null)
    requireActivity().unregisterReceiver(smsSendBroadcastReceiver)
    super.onDestroy()
  }

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

    btnSend.setOnClickListener {
      if (txtChipInstructions.isVisible) {
        showSnackbar(getString(R.string.please_select_a_station))
      }
      viewModel.onSendCommandClick("send")
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
      it?.let { stations ->
        if (it.isEmpty()) {
          cg.removeAllViews()
          stationsQueue.clear()
          return@let
        }

        txtChipInstructions.visibility = View.INVISIBLE

        stationsQueue.clear()
        stationsQueue.addAll(stations)

        for (s in stations) {
          cg.removeAllViews()
          addChips(stations)
        }
      }
    })

    viewModel.sendCommand.observe(this, Observer {
      it.getContentIfNotHandled()?.let {
        Log.d("SendCommand", "Sending command........")
        prepareMessages()
      }
    })

    viewModel.snackbarMessage.observe(this, Observer {
      it.getContentIfNotHandled()?.let { msg ->
        showSnackbar(msg)
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

  private fun showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
  }

  private fun prepareMessages() {
    messagesQueue.clear()
    for (s in stationsQueue) {
      messagesQueue.add(viewModel.command.value?.let { SmsEntity(s.mobileNo, it) })
    }

    processSms()
  }

  private fun processSms() {
    if (messagesQueue.isNotEmpty()) {
      messagesQueue.remove()?.let {
        startSmsWork(it)
      }
    }
  }

  private fun startSmsWork(sms: SmsEntity) {
    val gson = Gson()

    val smsWorkerRequestBuilder = OneTimeWorkRequestBuilder<SmsWorker>()
    smsWorkerRequestBuilder.setInputData(workDataOf(SMS_WORKER_INPUT_KEY to gson.toJson(sms)))

    WorkManager.getInstance().enqueueUniqueWork(
      SMS_UNIQUE_WORK,
      ExistingWorkPolicy.APPEND,
      smsWorkerRequestBuilder.build()
    )
  }

  override fun onSmsSent() {
    if (messagesQueue.isNotEmpty())
      processSms()
    else
      viewModel.setSnackbarMessage(getString(R.string.successfully_sent_command))
  }

  override fun onSmsFailed() {
    messagesQueue.clear()
    viewModel.setSnackbarMessage(getString(R.string.error_sendind_command))
  }

}
