package com.ralphevmanzano.awssmsgateway.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ralphevmanzano.awssmsgateway.data.CommandsRepo
import com.ralphevmanzano.awssmsgateway.models.Command
import com.ralphevmanzano.awssmsgateway.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CommandDetailsViewModel(application: Application) : AndroidViewModel(application), Observable {
  private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.add(callback)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    callbacks.remove(callback)
  }

  private val commandsRepo: CommandsRepo = CommandsRepo(application)
  private val disposable = CompositeDisposable()
  private val _snackbarMessage = MutableLiveData<Event<String>>()
  private val _actionType = MutableLiveData<Int>()
  private val _areFieldsEditable = MutableLiveData<Boolean>()

  private var commandId = -1
  val title = MutableLiveData<String>()
  val commandName = MutableLiveData<String>()
  val description = MutableLiveData<String>()
  val remarks = MutableLiveData<String>()

  val snackbarMessage: LiveData<Event<String>>
    get() = _snackbarMessage

  val actionType: LiveData<Int>
    get() = _actionType

  val areFieldsEditable: LiveData<Boolean>
    get() = _areFieldsEditable

  fun setAction(type: Int) {
    _actionType.value = type
  }

  fun setAreFieldsEditable(isEditable: Boolean) {
    _areFieldsEditable.value = isEditable
  }

  private fun isVerified(): Boolean {
    return !(commandName.value.isNullOrBlank() ||
        description.value.isNullOrBlank() ||
        remarks.value.isNullOrBlank())
  }

  fun addCommand() {
    if (!isVerified()) {
      _snackbarMessage.postValue(Event("Please fill up all the fields"))
      return
    }

    val command = Command(
      commandName.value!!,
      description.value!!,
      remarks.value!!
    )

    disposable.add(commandsRepo.addCommand(command)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Command added successfully!"))
        commandName.postValue("")
        description.postValue("")
        remarks.postValue("")
      }, { error ->
        _snackbarMessage.postValue(Event("Error adding new command"))
        Log.e("CommandsViewModel", "Error adding new command ${error.localizedMessage}")
      })
    )
  }

  fun updateCommand() {
    if (!isVerified()) {
      _snackbarMessage.postValue(Event("Please fill up all the fields"))
      return
    }

    val command = Command(
      commandName.value!!,
      description.value!!,
      remarks.value!!
    )

    if (commandId != -1) {
      command.id = commandId
    }

    Log.d("CommandDetailsVm", "Updating command")
    disposable.add(commandsRepo.updateCommand(command)
      .subscribeOn(Schedulers.io())
      .subscribe({
        _snackbarMessage.postValue(Event("Command updated successfully!"))
      }, { error ->
        _snackbarMessage.postValue(Event("Error updating new command"))
        Log.e("CommandsViewModel", "Error updating new command ${error.localizedMessage}")
      }))
  }

  fun getCommand(id: Int) {
    disposable.add(commandsRepo.getCommand(id)
      .subscribeOn(Schedulers.io())
      .subscribe({
        commandId = it.id
        commandName.postValue(it.commandName)
        description.postValue(it.description)
        remarks.postValue(it.remarks)
      }, {
        _snackbarMessage.postValue(Event("Error getting command: ${it.localizedMessage}"))
        Log.e("CommandsViewModel", "Error getting command: ${it.localizedMessage}")
      })
    )
  }

  override fun onCleared() {
    disposable.clear()
  }
}
