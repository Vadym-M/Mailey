package com.devx.mailey.presentation.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.Event
import com.devx.mailey.util.Navigator
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository) :
    ViewModel(){
    private var currentUser: User? = null
    private var userJob: Job? = null

    private var string: String? = null

    private var chatPair: Pair<String ,User>? = null

    private var roomId: String? = null


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _fragment = MutableLiveData<Event<Fragment?>>()
    val onFragmentChanged: LiveData<Event<Fragment?>>
        get() = _fragment

    private val _backPressed = MutableLiveData<Event<String>>()
    val backPressed: LiveData<Event<String>>
        get() = _backPressed

    private val _onRoomChanged = MutableLiveData<String>()
    val onRoomChanged: LiveData<String>
        get() = _onRoomChanged

    private val _roomsChanged = MutableLiveData<MutableList<RoomItem>>()
    val roomsChanged: LiveData<MutableList<RoomItem>>
        get() = _roomsChanged


    fun getCurrentUser():User{
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

    fun setFragment(fragment: Fragment?){
     _fragment.value = Event(fragment)
    }
    fun backPressed(){
        _backPressed.value = Event("")
    }

    fun putString(str:String){
        string = str
    }
    fun getString(): String?{
        return string
    }
    fun putChatPair(pair: Pair<String, User>){
        chatPair = pair
    }
    fun getChatPair(): Pair<String, User>?{
        return chatPair
    }
    fun putRoomId(roomId:String){
        this.roomId = roomId
    }
    fun getRoom(): Room? {
        val rooms = databaseRepository.getRooms()
        return rooms.find { it.roomId == roomId }
    }


    private fun onRoomsChanged(userId: String) = viewModelScope.launch{
        val result = databaseRepository.onRoomsChanged(_onRoomChanged, userId)
        onRoomChanged.observeForever{
            viewModelScope.launch {
                databaseRepository.getRoomById(it)
                val list = databaseRepository.getRooms()
                val newList = mutableListOf<RoomItem>()
                list.forEach {
                    if(it.firstUserId == currentUser!!.id){
                        newList.add(RoomItem(it.roomId, it.secondUserUrl, it.secondUserName, it.messages.getLastMessage(), it.messages.getLastMessageTimestamp()))
                    }else{
                        newList.add(RoomItem(it.roomId, it.firstUserUrl, it.firstUserName, it.messages.getLastMessage(), it.messages.getLastMessageTimestamp()))
                    }

                }
                //newList.sortWith(compareBy { it.messages.values.first().timestamp })
                _roomsChanged.postValue(newList)
            }
        }

    }

}