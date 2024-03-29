package com.ralphevmanzano.awssmsgateway.ui.adapters

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.StationItemBinding
import com.ralphevmanzano.awssmsgateway.models.Station
import com.ralphevmanzano.awssmsgateway.ui.viewholder.BaseViewHolder
import com.ralphevmanzano.awssmsgateway.utils.OnMenuClickListener

class StationsAdapter(private val listener: OnMenuClickListener, diffCallback: DiffUtil.ItemCallback<Station>): BaseAdapter<Station>(diffCallback) {
  override fun getItemViewType(position: Int): Int {
    return R.layout.station_item
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    binding.setVariable(BR.station, getItem(position))
    binding.executePendingBindings()
  }

  override fun setListeners(binding: ViewDataBinding, holder: BaseViewHolder) {
    if (binding is StationItemBinding) {
      binding.btnMenu.setOnClickListener {
        listener.onMenuClick(holder.adapterPosition)
      }
    }
  }
}