package com.ralphevmanzano.awssmsgateway.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ralphevmanzano.awssmsgateway.R

/**
 * A simple [Fragment] subclass.
 *
 */
class MaintenanceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false)
    }


}
