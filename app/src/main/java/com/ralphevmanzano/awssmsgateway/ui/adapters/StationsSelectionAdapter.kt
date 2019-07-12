package com.ralphevmanzano.awssmsgateway.ui.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.StationSelectionItemBinding
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.viewholder.BaseViewHolder

class StationsSelectionAdapter(private val listener: OnStationCheckedListener, diffCallback: DiffUtil.ItemCallback<Station>) : BaseAdapter<Station>(diffCallback) {

  interface OnStationCheckedListener {
    fun onStationChecked(isChecked: Boolean, station: Station)
  }

  override fun getItemViewType(position: Int): Int {
    return R.layout.station_selection_item
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    binding.setVariable(BR.station, getItem(position))
    binding.executePendingBindings()
  }

  override fun setListeners(binding: ViewDataBinding, holder: BaseViewHolder) {
    if (binding is StationSelectionItemBinding) {
      binding.root.setOnClickListener {
        binding.cb.performClick()
      }

      binding.cb.setOnCheckedChangeListener { _, b ->
        listener.onStationChecked(b, getItem(holder.adapterPosition))
      }
    }
  }
}