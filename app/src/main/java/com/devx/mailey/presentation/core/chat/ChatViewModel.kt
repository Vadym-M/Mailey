package com.devx.mailey.presentation.core.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ChatViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    private val _onMessageAdded = MutableLiveData<MutableList<ChatItems<Message>>>()
    val onMessageAdded: LiveData<MutableList<ChatItems<Message>>>
        get() = _onMessageAdded

    private val _onMessageListener = MutableLiveData<MutableMap<String, Message>>()
    private val onMessageListener: LiveData<MutableMap<String, Message>>
        get() = _onMessageListener


    private var currentMessages = mutableListOf<ChatItems<Message>>()

    var user: User? = null
    private var room: Room? = null
    fun initCurrentUser(user: User) {
        this.user = user
    }

    fun initRoom(roomId: String, userId: String) {
        getRoom(roomId, userId)

    }

    private fun getRoom(roomId: String, userId: String) = viewModelScope.launch {
        try {
            room = databaseRepository.getRoomById(roomId)
            showMessages()
        } catch (e: Exception) {
            room = Room(HashMap(), roomId, userId, user!!.id)
            showMessages()
            databaseRepository.createRoom(room!!)
            databaseRepository.pushRoomIdToUser(roomId, user!!.id)
        }
        messageListener(roomId)
    }

    private fun showMessages() {
        if (currentMessages.isNullOrEmpty()) {
            val listMsg = room!!.messages.values.toMutableList()
            val list = mutableListOf<ChatItems<Message>>()
            listMsg.forEach { item ->
                if (item.userId == user!!.id) {
                    list.add(ChatItems.UserRight(item))
                } else {
                    list.add(ChatItems.UserLeft(item))
                }
            }
            currentMessages = list
        }
        currentMessages.sortWith(compareBy { it.data?.timestamp })
        currentMessages.reverse()
        _onMessageAdded.postValue(currentMessages)
    }

    fun sendMessage(str: String) {
        val uniqueID = UUID.randomUUID().toString()
        val imageUrl =
            if (user!!.imagesUrl.isNotEmpty()) user!!.imagesUrl.first() else Constants.IMAGE_BLANK_URL
        val msg = Message(uniqueID, str, user!!.id, Date().time, imageUrl, user!!.fullName)
        currentMessages.add(0, ChatItems.UserRight(msg))
        _onMessageAdded.postValue(currentMessages)
        pushMessage(room!!.roomId, msg)
    }

    private fun messageListener(roomId: String) {
        databaseRepository.addMessageListener(_onMessageListener, roomId)
        onMessageListener.observeForever { it ->
            if (currentMessages.isNullOrEmpty()) {
                currentMessages.add(0, ChatItems.UserLeft(it.values.first()))
            }
            it.forEach {
                if (it.value.userId != user?.id) {
                    if (currentMessages.first().data?.timestamp != it.value.timestamp) {
                        currentMessages.add(0, ChatItems.UserLeft(it.value))
                    }
                }
            }
            _onMessageAdded.postValue(currentMessages)
        }
    }


    private fun pushMessage(roomId: String, msg: Message) = viewModelScope.launch {
        databaseRepository.pushMessage(roomId, msg)
    }

}