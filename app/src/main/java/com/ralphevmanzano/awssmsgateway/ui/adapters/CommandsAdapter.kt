package com.ralphevmanzano.awssmsgateway.ui.adapters

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.ralphevmanzano.awssmsgateway.BR
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.databinding.CommandItemBinding
import com.ralphevmanzano.awssmsgateway.databinding.StationItemBinding
import com.ralphevmanzano.awssmsgateway.models.Command
import com.ralphevmanzano.awssmsgateway.ui.viewholder.BaseViewHolder
import com.ralphevmanzano.awssmsgateway.utils.OnMenuClickListener

class CommandsAdapter(private val listener: OnMenuClickListener, diffCallback: DiffUtil.ItemCallback<Command>): BaseAdapter<Command>(diffCallback) {
  private var commandClickListener: OnCommandClickListener? = null

  interface OnCommandClickListener {

    fun onCommandClick(command: Command)
  }
  fun setOnCommandClickListener(listener: OnCommandClickListener) {
    commandClickListener = listener
  }

  override fun getItemViewType(position: Int): Int {
    return R.layout.command_item
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    binding.setVariable(BR.command, getItem(position))
    binding.executePendingBindings()
  }

  override fun setListeners(binding: ViewDataBinding, holder: BaseViewHolder) {
    if (binding is CommandItemBinding) {
      binding.btnMenu.setOnClickListener {
        listener.onMenuClick(holder.adapterPosition)
      }

      binding.root.setOnClickListener {
        commandClickListener?.onCommandClick(getItem(holder.adapterPosition))
      }
    }
  }

}