package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ralphevmanzano.awssmsgateway.data.CommandsRepo
import com.ralphevmanzano.awssmsgateway.models.Command
import com.ralphevmanzano.awssmsgateway.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CommandsViewModel(application: Application) : AndroidViewModel(application) {
  private val commandsRepo: CommandsRepo = CommandsRepo(application)
  private val disposable = CompositeDisposable()

  private val _openMoreDialog = MutableLiveData<Event<Int>>()
  private val _navigateToDetails = MutableLiveData<Event<Int>>()
  private val _navigateToSend = MutableLiveData<Event<Command>>()
  private val _selectedItem = MutableLiveData<Int>()
  private val _snackbarMessage = MutableLiveData<Event<String>>()
  private val _commands = MutableLiveData<List<Command>>()

  val openMoreDialog: LiveData<Event<Int>>
    get() = _openMoreDialog

  val navigateToDetails: LiveData<Event<Int>>
    get() = _navigateToDetails

  val selectedItem: LiveData<Int>
    get() = _selectedItem

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  val navigateToSend: LiveData<Event<Command>>
    get() = _navigateToSend

  fun selectItem(id: Int) {
    _selectedItem.value = id
  }

  fun onNavigateToDetails(id: Int) {
    _navigateToDetails.value = Event(id)
  }

  fun onNavigateSendClick(command: Command) {
    _navigateToSend.value = Event(command)
  }

  fun onOpenDialogClick(itemId: Int) {
    _openMoreDialog.value = Event(itemId)  // Trigger the event by setting a new Event as a new value
  }

  fun getCommands(): LiveData<List<Command>> {
    disposable.add(commandsRepo.getCommands()
      .subscribeOn(Schedulers.io())
      .subscribe({ stations ->
        _commands.postValue(stations)
      }, { error ->
        _snackbarMessage.postValue(Event("Error fetching commands"))
        Log.e("CommandsViewModel", "Error fetching commands: ${error.localizedMessage}")
      })
    )

    return _commands
  }

  fun deleteStation(command: Command) {
    disposable.add(commandsRepo.deleteCommand(command)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Successfully deleted ${command.commandName}!"))
      }, { error ->
        _snackbarMessage.postValue(Event("Error deleting ${command.commandName}"))
        Log.e("StationsViewModel", "Error deleting station: ${error.localizedMessage}")
      }))
  }

  override fun onCleared() {
    disposable.clear()
  }
}
