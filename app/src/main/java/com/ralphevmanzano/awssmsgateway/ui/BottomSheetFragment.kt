package com.ralphevmanzano.awssmsgateway.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ralphevmanzano.awssmsgateway.R
import kotlinx.android.synthetic.main.bottom_sheet.view.*

class BottomSheetFragment(private val listener: OnStationClickListener): BottomSheetDialogFragment() {

  interface OnStationClickListener {
    fun onView()
    fun onEdit()
    fun onDelete()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.bottom_sheet, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    view.btnView.setOnClickListener {
      listener.onView()
      dismiss()
    }

    view.btnEdit.setOnClickListener {
      listener.onEdit()
      dismiss()
    }

    view.btnDelete.setOnClickListener {
      listener.onDelete()
      dismiss()
    }
  }
}