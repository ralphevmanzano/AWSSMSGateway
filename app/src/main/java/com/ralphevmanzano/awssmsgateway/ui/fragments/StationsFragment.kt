package com.ralphevmanzano.awssmsgateway.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.BottomSheetFragment
import com.ralphevmanzano.awssmsgateway.ui.adapters.StationsAdapter
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationsViewModel
import com.ralphevmanzano.awssmsgateway.utils.ItemDecoration
import com.ralphevmanzano.awssmsgateway.utils.OnMenuClickListener
import com.ralphevmanzano.awssmsgateway.utils.StationDiffUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*

class StationsFragment : Fragment(), OnMenuClickListener,
  BottomSheetFragment.OnSheetClickListener {

  private lateinit var adapter: StationsAdapter
  private lateinit var viewModel: StationsViewModel
  private var stations: ArrayList<Station> = ArrayList()

  companion object {
    const val STATIONS_VIEW = 0
    const val STATIONS_EDIT = 1
    const val STATIONS_ADD = 2
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_list, container, false)
  }

  override fun onStart() {
    super.onStart()
    activity?.toolbar?.title = getString(R.string.stations)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    txtTitle.text = getString(R.string.list_of_stations)
    initRv()
    initListeners()
  }

  private fun initRv() {
    adapter = StationsAdapter(this, StationDiffUtil())

    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.divider_black) }
    drawable?.let { rv.addItemDecoration(ItemDecoration(it)) }

    rv.layoutManager = LinearLayoutManager(context)
    rv.adapter = adapter
  }

  private fun initListeners() {
    btnAdd.setOnClickListener {
      viewModel.selectItem(-1)
      viewModel.onDetailsClick(STATIONS_ADD)
    }
  }


  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(StationsViewModel::class.java)

    viewModel.getStations().observe(this, Observer {
      it?.let {
        adapter.submitList(it)

        stations.clear()
        stations.addAll(it)
      }
    })

    viewModel.navigateToDetails.observe(this, Observer {
      Log.d("Stations", "Navigate to edit click")
      it.getContentIfNotHandled()?.let { type ->
        val action =
          viewModel.selectedItem.value?.let { pos ->
            var id = -1
            if (pos != -1) {
              id = stations[pos].id
            }
            StationsFragmentDirections.stationDetailsAction(
              type,
              id
            )
          }
        action?.let { act -> findNavController(this).navigate(act) }
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

  override fun onView() {
    viewModel.selectedItem.value?.let { viewModel.onDetailsClick(STATIONS_VIEW) }
  }

  override fun onEdit() {
    viewModel.selectedItem.value?.let { viewModel.onDetailsClick(STATIONS_EDIT) }
  }

  override fun onDelete() {
    showDeleteDialog()
  }

  private fun showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
  }

  @SuppressLint("InflateParams")
  override fun onMenuClick(position: Int) {
    viewModel.onOpenDialogClick(position)
    viewModel.selectItem(position)
  }

  @SuppressLint("InflateParams")
  private fun openBottomSheet() {
    val bottomSheetFragment = BottomSheetFragment(this)
    fragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.tag) }
  }

  private fun showDeleteDialog() {
    val builder = AlertDialog.Builder(context)
    var station: Station? = null
    viewModel.selectedItem.value?.let { pos ->
      station = stations[pos]
    }

    with(builder) {
      setIcon(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_close) })
      setTitle(getString(R.string.delete_station))
      setMessage(getString(R.string.are_you_sure_you_want_to_delete, station?.stationName))
      setPositiveButton(R.string.yes) { _, _ ->
        station?.let { viewModel.deleteStation(it) }
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
