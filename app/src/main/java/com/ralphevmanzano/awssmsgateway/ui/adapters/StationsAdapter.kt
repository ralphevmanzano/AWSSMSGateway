package com.ralphevmanzano.awssmsgateway.ui.adapters

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.StationItemBinding
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.viewholder.BaseViewHolder

class StationsAdapter(val listener: OnMenuClickListener, diffCallback: DiffUtil.ItemCallback<Station>): BaseAdapter<Station>(diffCallback) {

  interface OnMenuClickListener {
    fun onMenuClick(position: Int)
  }

  override fun getItemViewType(position: Int): Int {
    return R.layout.station_item
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    binding.setVariable(BR.station, getItem(position))
    binding.executePendingBindings()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
    val holder = super.onCreateViewHolder(parent, viewType)
    val binding = holder.binding
    if (binding is StationItemBinding) {
      binding.btnMenu.setOnClickListener {
        listener.onMenuClick(holder.adapterPosition)
      }
    }
    return holder
  }
}