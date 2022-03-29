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

    private val _initUser = MutableLiveData<Pair<String, String>>()
    val initUser: LiveData<Pair<String, String>>
        get() = _initUser



    private var currentMessages = mutableListOf<ChatItems<Message>>()

    var currentUser: User? = null
    var chatWithUserId: String? = null
    private var room: Room? = null
    fun initCurrentUser(user: User) {
        this.currentUser = user
    }

    fun initRoom(room: Room?, roomId: String?, user: User?) {
        if(room != null){
            this.room = room
            if(currentUser!!.id != room.firstUserId){
                _initUser.value = Pair(room.firstUserName, room.firstUserUrl)
                chatWithUserId = room.secondUserId
            }else{
                _initUser.value = Pair(room.secondUserName, room.secondUserUrl)
                chatWithUserId = room.firstUserId
            }
            showMessages()
            messageListener(room.roomId)
        }else {
            getRoom(roomId!!, user!!)
            val imageUrl = if(user.imagesUrl.isNotEmpty()) user.imagesUrl.last() else Constants.IMAGE_BLANK_URL
            _initUser.value = Pair(user.fullName, imageUrl)
            chatWithUserId = user.id
        }
    }

    private fun getRoom(roomId: String, user: User) = viewModelScope.launch {

            room = databaseRepository.getRoomById(roomId)
            if(room != null){
                showMessages()
            }else{
                val imageUrlFirst =
                    if (user.imagesUrl.isNotEmpty()) user.imagesUrl.last() else Constants.IMAGE_BLANK_URL
                val imageUrlSecond = if (currentUser!!.imagesUrl.isNotEmpty()) currentUser!!.imagesUrl.last() else Constants.IMAGE_BLANK_URL
                room = Room(HashMap(), roomId, user.id, user.fullName, imageUrlFirst, currentUser!!.id, currentUser!!.fullName, imageUrlSecond)
                showMessages()
                databaseRepository.createRoom(room!!)
                databaseRepository.pushRoomIdToUser(roomId, currentUser!!.id)
            }


        messageListener(roomId)
    }

    private fun showMessages() {
        if (currentMessages.isNullOrEmpty()) {
            val listMsg = room!!.messages.values.toMutableList()
            val list = mutableListOf<ChatItems<Message>>()
            listMsg.forEach { item ->
                if (item.userId == currentUser!!.id) {
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
            if (currentUser!!.imagesUrl.isNotEmpty()) currentUser!!.imagesUrl.last() else Constants.IMAGE_BLANK_URL
        val msg = Message(uniqueID, str, currentUser!!.id, Date().time, imageUrl, currentUser!!.fullName)
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
                if (it.value.userId != currentUser?.id) {
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
        databaseRepository.pushRoomIdToUser(roomId, chatWithUserId!!)
        databaseRepository.pushRoomIdToUser(roomId, currentUser!!.id)
    }

}