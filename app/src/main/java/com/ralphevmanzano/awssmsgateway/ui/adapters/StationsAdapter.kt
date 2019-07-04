package com.ralphevmanzano.awssmsgateway.ui.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.models.Station

class StationsAdapter(diffCallback: DiffUtil.ItemCallback<Station>): BaseAdapter<Station>(diffCallback) {

  override fun getItemViewType(position: Int): Int {
    return R.layout.station_item
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    binding.setVariable(BR.station, getItem(position))
    binding.executePendingBindings()
  }
}