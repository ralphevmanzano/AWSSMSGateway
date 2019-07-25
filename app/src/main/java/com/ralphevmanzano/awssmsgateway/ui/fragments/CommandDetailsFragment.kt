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
import com.ralphevmanzano.awssmsgateway.databinding.FragmentCommandDetailsBinding
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.CommandDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_station_details.*

class CommandDetailsFragment : Fragment() {

  private val args: CommandDetailsFragmentArgs by navArgs()
  private lateinit var binding: FragmentCommandDetailsBinding
  private lateinit var viewModel: CommandDetailsViewModel


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_command_details, container, false)
    return binding.root
  }

  override fun onStart() {2
    super.onStart()
    activity?.toolbar?.let {
      when(args.type) {
        CommandsFragment.COMMAND_VIEW -> it.title = getString(R.string.view_command)
        CommandsFragment.COMMAND_EDIT -> it.title = getString(R.string.edit_command)
        CommandsFragment.COMMAND_ADD -> it.title = getString(R.string.add_new_command)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initListeners()
  }

  private fun initListeners() {
    btnSave.setOnClickListener {

      if (args.type == CommandsFragment.COMMAND_ADD) {
        viewModel.addCommand()

      }
      else if (args.type == CommandsFragment.COMMAND_EDIT) {
        viewModel.updateCommand()
      }
    }
    btnCancel.setOnClickListener { findNavController().popBackStack() }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(CommandDetailsViewModel::class.java)

    binding.viewModel = viewModel
    binding.lifecycleOwner = viewLifecycleOwner

    viewModel.setAction(args.type)

    viewModel.actionType.observe(this, Observer {
      when(it) {
        CommandsFragment.COMMAND_VIEW -> {
          viewModel.title.value = getString(R.string.view)
          viewModel.setAreFieldsEditable(false)
          viewModel.getCommand(args.id)
        }
        CommandsFragment.COMMAND_EDIT  -> {
          viewModel.title.value = getString(R.string.edit)
          viewModel.setAreFieldsEditable(true)
          viewModel.getCommand(args.id)
        }
        CommandsFragment.COMMAND_ADD -> {
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
