package com.devx.mailey.presentation.core.chat

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.ChatItems
import com.devx.mailey.domain.data.LocalRoom
import com.devx.mailey.util.getUserImage
import com.devx.mailey.util.sortByTimestamp
import com.devx.mailey.util.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    private val _onMessageAdded = MutableLiveData<MutableList<ChatItems<Message>>>()
    val onMessageAdded: LiveData<MutableList<ChatItems<Message>>>
        get() = _onMessageAdded

    private val _onMessageListener = MutableLiveData<MutableMap<String, Message>>()
    private val onMessageListener: LiveData<MutableMap<String, Message>>
        get() = _onMessageListener

    private val _initUser = MutableLiveData<Pair<String, String>>()
    val initUser: LiveData<Pair<String, String>>
        get() = _initUser

    private var currentMessages = mutableListOf<ChatItems<Message>>()

    private var currentUser: User? = null
    private var chatWithUserId: String? = null
    private var room: Room? = null
    fun initCurrentUser(user: User) {
        this.currentUser = user
    }

    fun initRoom(localRoom: LocalRoom) {
        _initUser.value = Pair(localRoom.chatWithName, localRoom.chatWithImageUrl)
        chatWithUserId = localRoom.chatWithId
        val roomFromCache = databaseRepository.getRoomFromCache(localRoom.roomId)
        if (roomFromCache != null) {
            this.room = roomFromCache
        } else {
            viewModelScope.launch {
                try {
                    room = databaseRepository.getRoomById(localRoom.roomId)
                } catch (e: Exception) {
                    room = Room(
                        HashMap(),
                        localRoom.roomId,
                        localRoom.chatWithId,
                        localRoom.chatWithName,
                        localRoom.chatWithImageUrl,
                        currentUser!!.id,
                        currentUser!!.fullName,
                        currentUser!!.imagesUrl.getUserImage()
                    )
                    databaseRepository.createRoom(room!!)
                    messageListener(localRoom.roomId)
                }
            }
        }
        if (room != null) {
            showMessages()
            messageListener(localRoom.roomId)
        }
    }


    private fun showMessages() {

        val cMessages = mutableListOf<ChatItems<Message>>()
        room!!.messages.values.toMutableList().forEach { item ->
            val chatItem = if (item.userId == currentUser!!.id) {
                ChatItems.UserRight(item)
            } else {
                ChatItems.UserLeft(item)
            }
            cMessages.add(chatItem)
        }
        val groups = cMessages.groupBy { it.data?.timestamp?.toDate() }

        groups.forEach{
            currentMessages.add(ChatItems.Other(it.value.first().data))
            currentMessages.addAll(it.value)
        }
        currentMessages.sortByTimestamp()
        _onMessageAdded.postValue(currentMessages)
    }

    fun sendMessage(str: String) {
        val currentUser = currentUser!!
        val currentRoom = room!!

        val msg = Message(
            id = UUID.randomUUID().toString(),
            text = str,
            timestamp = Date().time,
            userId = currentUser.id,
            userName = currentUser.fullName,
            imageUrl = currentUser.imagesUrl.getUserImage(),
        )
        if(currentMessages[0].data?.timestamp?.toDate() != msg.timestamp.toDate()){
            currentMessages.add(0, ChatItems.Other(msg))
        }
        currentMessages.add(0, ChatItems.UserRight(msg))
        _onMessageAdded.postValue(currentMessages)

        pushMessageToDatabase(currentRoom.roomId, msg)
    }

    private fun messageListener(roomId: String) {
        databaseRepository.addMessageListener(_onMessageListener, roomId)
        onMessageListener.observeForever { it ->
            if (currentMessages.isNullOrEmpty()) {
                currentMessages.add(0, ChatItems.UserLeft(it.values.first()))
            } else {
                it.forEach {
                    if (it.value.userId != currentUser?.id) {
                        if (currentMessages.first().data?.timestamp != it.value.timestamp) {
                            if(currentMessages[0].data?.timestamp?.toDate() != it.value.timestamp.toDate()){
                                currentMessages.add(0, ChatItems.Other(it.value))
                            }
                            currentMessages.add(0, ChatItems.UserLeft(it.value))
                        }
                    }
                }
            }
            _onMessageAdded.postValue(currentMessages)
        }
    }


    private fun pushMessageToDatabase(roomId: String, msg: Message) = viewModelScope.launch {
        databaseRepository.pushMessage(roomId, msg)
        databaseRepository.pushRoomIdToUser(roomId, chatWithUserId!!)
        databaseRepository.pushRoomIdToUser(roomId, currentUser!!.id)
    }

}