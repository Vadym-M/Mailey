package com.devx.mailey.presentation.core

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.LocalRoom
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.domain.usecases.GetRoomItemsUseCase
import com.devx.mailey.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val getRoomItemsUseCase: GetRoomItemsUseCase
) :
    ViewModel() {
    private var currentUser: User? = null
    private var userJob: Job? = null
    private var localRoomData: LocalRoom? = null
    private var fieldName: String? = null
    private var userRooms = mutableListOf<String>()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _fragment = MutableLiveData<Event<Fragment?>>()
    val onFragmentChanged: LiveData<Event<Fragment?>>
        get() = _fragment

    private val _backPressed = MutableLiveData<Event<String>>()
    val backPressed: LiveData<Event<String>>
        get() = _backPressed

    private val _roomsChanged = MutableLiveData<List<RoomItem>>()
    val roomsChanged: LiveData<List<RoomItem>>
        get() = _roomsChanged


    fun getCurrentUser(): User {
        return user.value!!
    }

    fun fetchCurrentUser() {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            val user = databaseRepository.getCurrentUserData()
            _user.postValue(user)
            currentUser = user
            onRoomsChanged(user.id)
        }
    }

    fun setFragment(fragment: Fragment?) {
        _fragment.value = Event(fragment)
    }

    fun backPressed() {
        _backPressed.value = Event("")
    }

    fun putRoomData(localRoom: LocalRoom) {
        this.localRoomData = localRoom
    }

    fun getRoomData(): LocalRoom? {
        return localRoomData
    }

    fun setFieldName(fieldName: String?) {
        this.fieldName = fieldName
    }

    fun getFieldName(): String? {
        return this.fieldName
    }


    private fun onRoomsChanged(userId: String) = viewModelScope.launch {
        val listener = databaseRepository.onRoomsChanged(userId)
        listener.observeForever { rooms ->
            currentUser!!.rooms = rooms

                getRoomItemsUseCase.getRoomItems(currentUser!!).onEach {
                    when(it){
                        is Resource.Loading ->{}
                        is Resource.Success -> { _roomsChanged.postValue(it.data!!)}
                        is Resource.Error -> {
                           }
                    }
                }.launchIn(viewModelScope)

        }
    }
}