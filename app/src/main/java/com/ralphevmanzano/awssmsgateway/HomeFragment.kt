package com.ralphevmanzano.awssmsgateway


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.work.*
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.models.User
import com.ralphevmanzano.awssmsgateway.utils.WORKER_INPUT_DATA
import com.ralphevmanzano.awssmsgateway.workers.ApiWorker
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_home, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initUI()
  }

  private fun initUI() {
    button.setOnClickListener {
      val user = User("Ralph", "Manzz", "Davs", "1234", "uy", "123")
      val data = Gson().toJson(user)

      val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

      val apiWorker = OneTimeWorkRequestBuilder<ApiWorker>()
        .setConstraints(constraints)
        .setInputData(workDataOf(WORKER_INPUT_DATA to data))
        .build()

      WorkManager.getInstance().enqueue(apiWorker)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.main_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_settings -> view?.let { Navigation.findNavController(it).navigate(R.id.action_home_settings) }
    }
    return super.onOptionsItemSelected(item)
  }
}
