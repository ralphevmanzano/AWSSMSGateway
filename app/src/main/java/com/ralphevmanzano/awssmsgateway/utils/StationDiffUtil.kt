package com.ralphevmanzano.awssmsgateway.utils

import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.models.Station

class StationDiffUtil : DiffUtil.ItemCallback<Station>() {
  override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
    return oldItem == newItem
  }
}