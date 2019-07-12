package com.ralphevmanzano.awssmsgateway.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.Command
import com.ralphevmanzano.awssmsgateway.ui.BottomSheetFragment
import com.ralphevmanzano.awssmsgateway.ui.adapters.CommandsAdapter
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.CommandsViewModel
import com.ralphevmanzano.awssmsgateway.utils.CommandDiffUtil
import com.ralphevmanzano.awssmsgateway.utils.ItemDecoration
import com.ralphevmanzano.awssmsgateway.utils.OnMenuClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*

class CommandsFragment : Fragment(), OnMenuClickListener, BottomSheetFragment.OnSheetClickListener, CommandsAdapter.OnCommandClickListener {

  private lateinit var adapter: CommandsAdapter
  private lateinit var viewModel: CommandsViewModel
  private var commands: ArrayList<Command> = ArrayList()

  companion object {
    const val COMMAND_VIEW = 0
    const val COMMAND_EDIT = 1
    const val COMMAND_ADD = 2
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_list, container, false)
  }

  override fun onStart() {
    super.onStart()
    activity?.toolbar?.title = getString(R.string.commands)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    txtTitle.text = getString(R.string.list_of_commands)
    initRv()
    initListeners()
  }

  private fun initRv() {
    adapter = CommandsAdapter(this, CommandDiffUtil())
    adapter.setOnCommandClickListener(this)

    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.divider_black)}
    drawable?.let { rv.addItemDecoration(ItemDecoration(it)) }

    rv.layoutManager = LinearLayoutManager(context)
    rv.adapter = adapter
  }

  private fun initListeners() {
    btnAdd.setOnClickListener {
      viewModel.selectItem(-1)
      viewModel.onNavigateToDetails(COMMAND_ADD)
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(CommandsViewModel::class.java)

    viewModel.getCommands().observe(this, Observer {
      it?.let {
        adapter.submitList(it)

        commands.clear()
        commands.addAll(it)
      }
    })

    viewModel.navigateToDetails.observe(this, Observer {
      it.getContentIfNotHandled()?.let { type ->
        navigateToDetails(type)
      }
    })

    viewModel.navigateToSend.observe(this, Observer {
      it.getContentIfNotHandled()?.let { command ->
        navigateToSendCommand(command)
      }
    })

    viewModel.openMoreDialog.observe(this, Observer {
      it.getContentIfNotHandled()?.let {
        openBottomSheet()
      }
    })

    viewModel.snackbarMessage.observe(this, Observer {
      it.getContentIfNotHandled()?.let { msg ->
        showSnackbar(msg)
      }
    })
  }

  private fun navigateToDetails(type: Int) {
    val action =
      viewModel.selectedItem.value?.let { pos ->
        var id = -1
        if (pos != -1) {
          id = commands[pos].id
        }
        CommandsFragmentDirections.commandDetailsAction(
          type,
          id
        )
      }
    action?.let { act -> NavHostFragment.findNavController(this).navigate(act) }
  }

  private fun navigateToSendCommand(command: Command) {
    val action = CommandsFragmentDirections.actionSendCommand(command.commandName)
    findNavController().navigate(action)
  }

  override fun onView() {
    viewModel.selectedItem.value?.let { viewModel.onNavigateToDetails(COMMAND_VIEW) }
  }

  override fun onEdit() {
    viewModel.selectedItem.value?.let { viewModel.onNavigateToDetails(COMMAND_EDIT) }
  }

  override fun onDelete() {
    showDeleteDialog()
  }

  override fun onMenuClick(position: Int) {
    viewModel.onOpenDialogClick(position)
    viewModel.selectItem(position)
  }

  override fun onCommandClick(command: Command) {
    viewModel.onNavigateSendClick(command)
  }

  @SuppressLint("InflateParams")
  private fun openBottomSheet() {
    val bottomSheetFragment = BottomSheetFragment(this)
    fragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.tag) }
  }

  private fun showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
  }

  private fun showDeleteDialog() {
    val builder = AlertDialog.Builder(context)
    var command: Command? = null
    viewModel.selectedItem.value?.let { pos ->
      command = commands[pos]
    }

    with(builder) {
      setIcon(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_close) })
      setTitle(getString(R.string.delete_station))
      setMessage(getString(R.string.are_you_sure_you_want_to_delete, command?.commandName))
      setPositiveButton(R.string.yes) { _, _ ->
        command?.let { viewModel.deleteStation(it) }
      }
      setNegativeButton(R.string.no) { _, _ -> }
    }

    val alertDialog = builder.create()
    alertDialog.show()

    val btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
    val btnNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

    context?.let {
      btnPositive.setTextColor(ContextCompat.getColorStateList(context!!, R.color.green))
      btnNegative.setTextColor(ContextCompat.getColor(context!!, R.color.btn_red))
    }
  }
}
