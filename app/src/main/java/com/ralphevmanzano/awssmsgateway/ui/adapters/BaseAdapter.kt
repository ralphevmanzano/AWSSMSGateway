package com.ralphevmanzano.awssmsgateway.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ralphevmanzano.awssmsgateway.ui.viewholder.BaseViewHolder
import com.ralphevmanzano.awssmsgateway.utils.OnItemClickListener


abstract class BaseAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, BaseViewHolder>(diffCallback) {
  private var onItemClickListener: OnItemClickListener? = null
  private lateinit var binding: ViewDataBinding

  abstract fun bind(binding: ViewDataBinding, position: Int)

  abstract override fun getItemViewType(position: Int): Int

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    binding = DataBindingUtil.inflate(inflater, viewType, parent, false)
    return BaseViewHolder(this.binding, onItemClickListener)
  }

  fun setOnItemclickListener(listener: OnItemClickListener?) {
    listener?.let {
      onItemClickListener = listener
    }
  }

  override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
    bind(holder.binding, holder.adapterPosition)
  }
}