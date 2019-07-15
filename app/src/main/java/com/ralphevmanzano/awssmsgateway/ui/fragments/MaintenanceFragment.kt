package com.ralphevmanzano.awssmsgateway.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ralphevmanzano.awssmsgateway.R
import kotlinx.android.synthetic.main.fragment_maintenance.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MaintenanceFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_maintenance, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    btnStations.setOnClickListener {
      findNavController().navigate(R.id.actionStations)
    }

    btnCommands.setOnClickListener {
      findNavController().navigate(R.id.actionCommands)
    }
  }

}
