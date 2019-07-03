package com.ralphevmanzano.awssmsgateway.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ralphevmanzano.awssmsgateway.utils.PreferenceHelper.get
import com.ralphevmanzano.awssmsgateway.utils.PreferenceHelper.set
import com.ralphevmanzano.awssmsgateway.R
import com.ralphevmanzano.awssmsgateway.ui.views.EditTextDialog
import com.ralphevmanzano.awssmsgateway.utils.DIALOG_SERVER_IP
import com.ralphevmanzano.awssmsgateway.utils.PREF_SERVER_IP
import com.ralphevmanzano.awssmsgateway.utils.PreferenceHelper.defaultPrefs
import io.reactivex.disposables.CompositeDisposable
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

    btnSettings.setOnClickListener {
      val pref = context?.let { t -> defaultPrefs(t) }
      val dialog = EditTextDialog.newInstance(getString(R.string.server_ip_address), null, pref?.get(PREF_SERVER_IP), false)

      dialog.onOk = {
        pref?.set(PREF_SERVER_IP, dialog.editText.text.toString())
      }

      fragmentManager?.let { f -> dialog.show(f, DIALOG_SERVER_IP) }
    }

    btnMaintenance.setOnClickListener {
      Navigation.findNavController(it).navigate(R.id.action_home_maintenance)
    }

    /*btnSettings.setOnClickListener {
      val dao = SmsDatabase.getInstance(it.context).smsDao()
      disposable.add(dao.deleteAll()
        .subscribeOn(Schedulers.io())
        .subscribe({
          Log.d("Room", "Successfully cleared the table!")
        }, { error ->
          Log.e("Room", "Error deleting entries: $error")
        }))
    }*/
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//    inflater.inflate(R.menu.main_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_settings -> view?.let { Navigation.findNavController(it).navigate(R.id.action_home_settings) }
    }
    return super.onOptionsItemSelected(item)
  }
}
