package com.ralphevmanzano.awssmsgateway.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ralphevmanzano.awssmsgateway.db.AwsDatabase
import com.ralphevmanzano.awssmsgateway.db.CommandDao
import com.ralphevmanzano.awssmsgateway.models.Command
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class CommandsRepo(context: Context) {
  private val commandDao: CommandDao = AwsDatabase.getInstance(context).commandDao()

  private val commandsList = MutableLiveData<List<Command>>()

  fun addCommand(command: Command): Completable {
    return commandDao.insert(command)
  }

  fun getCommand(id: Int): Single<Command> {
    return commandDao.getCommand(id)
  }

  fun getCommands(): Flowable<List<Command>> {
    return commandDao.getCommands()
  }

  fun updateCommand(command: Command) : Completable {
    return commandDao.update(command)
  }

  fun deleteCommand(command: Command): Completable {
    return commandDao.delete(command)
  }
}