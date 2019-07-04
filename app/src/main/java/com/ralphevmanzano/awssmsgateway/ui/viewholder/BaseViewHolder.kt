package com.ralphevmanzano.awssmsgateway.ui.viewholder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ralphevmanzano.awssmsgateway.utils.OnItemClickListener

class BaseViewHolder(var binding: ViewDataBinding, listener: OnItemClickListener?) : RecyclerView.ViewHolder(binding.root) {

  init {
    listener?.let {
      binding.root.setOnClickListener {
        listener.onItemClick(adapterPosition)
      }
    }
  }
}