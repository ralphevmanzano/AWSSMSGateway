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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.BottomSheetFragment
import com.ralphevmanzano.awssmsgateway.ui.adapters.StationsAdapter
import com.ralphevmanzano.awssmsgateway.ui.viewmodels.StationsViewModel
import kotlinx.android.synthetic.main.fragment_stations.*

class StationsFragment : Fragment(), StationsAdapter.OnMenuClickListener,
  BottomSheetFragment.OnStationClickListener {

  private lateinit var adapter: StationsAdapter
  private lateinit var viewModel: StationsViewModel
  private var stations: ArrayList<Station> = ArrayList()

  private val bottomSheetDialog: BottomSheetDialog?
    get() {
      return context?.let { BottomSheetDialog(it) }
    }

  companion object {
    fun newInstance() = StationsFragment()

    const val STATION_VIEW = 0
    const val STATION_EDIT = 1
    const val STATION_ADD = 2
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_stations, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initRv()
    initListeners()
  }

  private fun initRv() {
    rv.layoutManager = LinearLayoutManager(context)
    adapter = StationsAdapter(this, object : DiffUtil.ItemCallback<Station>() {
      override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
      }

    })
    rv.adapter = adapter
  }

  private fun initListeners() {
    btnAdd.setOnClickListener {
      viewModel.selectItem(-1)
      viewModel.onEditClick(STATION_ADD)
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

    viewModel.openMoreDialog.observe(this, Observer {
      it.getContentIfNotHandled()?.let {
        openBottomSheet()
      }
    })

    viewModel.navigateToEdit.observe(this, Observer {
      Log.d("Stations", "Navigate to edit click")
      it.getContentIfNotHandled()?.let { type ->
        val action =
          viewModel.selectedItem.value?.let { pos ->
            var id = -1
            if (pos != -1) {
              id = stations[pos].id
            }
            StationsFragmentDirections.detailsAction(
              type,
              id
            )
          }
        action?.let { act -> findNavController(this).navigate(act) }
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

  override fun onView() {
    viewModel.selectedItem.value?.let { viewModel.onEditClick(STATION_VIEW) }
  }

  override fun onEdit() {
    viewModel.selectedItem.value?.let { viewModel.onEditClick(STATION_EDIT) }
  }

  override fun onDelete() {
    showDeleteDialog()
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
      btnPositive.setTextColor(ContextCompat.getColorStateList(context!!, R.color.btn_blue))
      btnNegative.setTextColor(ContextCompat.getColor(context!!, R.color.btn_red))
    }


  }

}
