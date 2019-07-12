package com.ralphevmanzano.awssmsgateway.utils

import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.models.Command

class CommandDiffUtil : DiffUtil.ItemCallback<Command>() {
  override fun areItemsTheSame(oldItem: Command, newItem: Command): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Command, newItem: Command): Boolean {
    return oldItem == newItem
  }
}