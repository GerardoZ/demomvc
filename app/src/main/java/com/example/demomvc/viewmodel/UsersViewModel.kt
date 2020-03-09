package com.example.demomvc.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.demomvc.database.User
import com.example.demomvc.database.UsersDao
import kotlinx.coroutines.*


class UsersViewModel(val database: UsersDao, application: Application) : AndroidViewModel(application) {

    private val nameData: ObservableField<String> = ObservableField()
    val name: String
        get() = nameData.get().orEmpty()

    private val ageData: ObservableField<String> = ObservableField()
    val age: String
        get() = ageData.get().orEmpty()

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val users = database.getAllUsers()

    val deleteButtonVisible: LiveData<Boolean> = Transformations.map(users) {
        it != null && it.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun setNameValue(s: CharSequence, start: Int, before: Int, count: Int) {
        nameData.set(s.toString())
    }

    fun setAgeValue(s: CharSequence, start: Int, before: Int, count: Int) {
        ageData.set(s.toString())
    }


    fun onInsert() {
        val user = User(0, nameData.get().orEmpty(), ageData.get().orEmpty())
        uiScope.launch {
            insert(user)
        }
    }

    private suspend fun insert(user: User) {
        withContext(Dispatchers.IO) {
            if(user.name.isNotEmpty() && user.age.isNotEmpty()){
                database.insert(user)
            }
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            _showSnackbarEvent.value = true
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

