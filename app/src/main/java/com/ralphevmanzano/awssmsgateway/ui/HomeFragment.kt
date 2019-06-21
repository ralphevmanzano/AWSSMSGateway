package com.ralphevmanzano.awssmsgateway.ui


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.work.*
import com.google.gson.Gson
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.db.SmsDatabase
import com.ralphevmanzano.awssmsgateway.models.User
import com.ralphevmanzano.awssmsgateway.utils.API_WORKER_INPUT_KEY
import com.ralphevmanzano.awssmsgateway.workers.ApiWorker
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.clear()
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
        .setInputData(workDataOf(API_WORKER_INPUT_KEY to data))
        .build()

      WorkManager.getInstance().enqueue(apiWorker)
    }

    btnClear.setOnClickListener {
      val dao = SmsDatabase.getInstance(it.context).smsDao()
      disposable.add(dao.deleteAll()
        .subscribeOn(Schedulers.io())
        .subscribe({
          Log.d("Room", "Successfully cleared the table!")
        }, { error ->
          Log.e("Room", "Error deleting entries: $error")
        }))
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
