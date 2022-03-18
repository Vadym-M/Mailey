package com.devx.mailey.presentation.core.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.Constants
import com.devx.mailey.util.ResultState
import com.devx.mailey.util.reverseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
@HiltViewModel
class ChatViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    private val _onMessageAdded = MutableLiveData<MutableList<ChatItems<Message>>>()
    val onMessageAdded: LiveData<MutableList<ChatItems<Message>>>
        get() = _onMessageAdded

    private val _onMessageListener = MutableLiveData<MutableMap<String, Message>>()
    val onMessageListener: LiveData<MutableMap<String, Message>>
        get() = _onMessageListener

    private val _currentRoom = MutableLiveData<Room>()
    val currentRoom: LiveData<Room>
        get() = _currentRoom

    var user: User? = null
    var room: Room? = null

    fun initRoom(roomId: String, userId:String) {
        getRoom(roomId, userId)
    }
    private fun getRoom(roomId: String, userId:String) = viewModelScope.launch{
        user = databaseRepository.getCurrentUserData()
        val result = databaseRepository.isRoomExist(roomId)
        if(result){
            room = databaseRepository.getRoomById(roomId)
            showMessages()
        }else{
            room = Room(HashMap(), roomId, userId, user!!.id)
            showMessages()
                async { databaseRepository.createRoom(room!!)}
                async { databaseRepository.pushRoomIdToUser(roomId, user!!.id) }
        }
        messageListener()
    }
    private fun showMessages(){
        val list = mutableListOf<ChatItems<Message>>()
        room!!.messages.keys.sortedByDescending { it }
        room!!.messages.values.forEach{ item->

                if (item.userId == user!!.id) {
                    list.add(ChatItems.UserRight(item))
                } else {
                    list.add(ChatItems.UserLeft(item))
                }

        }
//        list.sortByDescending {
//            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC)
//            LocalDateTime.parse(it.data?.timestamp.toString(), pattern)}
        _onMessageAdded.postValue(list)
    }

    fun sendMessage(str: String){
        var uniqueID = UUID.randomUUID().toString()

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC)
        val formatted = current.format(formatter)
        val msg = Message(uniqueID, str, user!!.id, formatted, Constants.IMAGE_BLANK_URL, user!!.fullName)
        pushMessage(room!!.roomId, msg)
        room!!.messages.put(msg.id, msg)
        val list = mutableListOf<ChatItems<Message>>()
        room!!.messages.values.forEach{ item->

                if (item.userId == user!!.id) {
                    list.add(ChatItems.UserRight(item))
                } else {
                    list.add(ChatItems.UserLeft(item))
                }

        }
        list.sortByDescending {
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC)
            LocalDateTime.parse(it.data?.timestamp.toString(), pattern)

        }
        _onMessageAdded.postValue(list)
    }
    private fun messageListener(){
        databaseRepository.addMessageListener(_onMessageListener)
        onMessageListener.observeForever { it ->
            it.forEach {
                if(it.value.userId != user?.id){
                    room!!.messages.put(it.key, it.value)
                }

            }
            val list = mutableListOf<ChatItems<Message>>()
            room!!.messages.values.forEach{ item->

                    if (item.userId == user!!.id) {
                        list.add(ChatItems.UserRight(item))
                    } else {
                        list.add(ChatItems.UserLeft(item))
                    }

            }
            list.sortByDescending {
                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC)
                LocalDateTime.parse(it.data?.timestamp.toString(), pattern)

            }
            _onMessageAdded.postValue(list)
        }
    }


    private fun pushMessage(roomId:String, msg:Message) = viewModelScope.launch{
        databaseRepository.pushMessage(roomId, msg)
    }

}