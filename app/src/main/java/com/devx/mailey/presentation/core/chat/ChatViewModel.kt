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
import com.devx.mailey.domain.usecases.GetMessagesUseCase
import com.devx.mailey.domain.usecases.GetRoomItemsUseCase
import com.devx.mailey.domain.usecases.GetRoomUseCase
import com.devx.mailey.domain.usecases.RoomListenerUseCase
import com.devx.mailey.util.Resource
import com.devx.mailey.util.getUserImage
import com.devx.mailey.util.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Error
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val roomListenerUseCase: RoomListenerUseCase,
    private val getRoomUseCase: GetRoomUseCase
) :
    ViewModel() {

    private val _onMessageAdded = MutableLiveData<MutableList<ChatItems<Message>>>()
    val onMessageAdded: LiveData<MutableList<ChatItems<Message>>>
        get() = _onMessageAdded

    private val _initUser = MutableLiveData<Pair<String, String>>()
    val initUser: LiveData<Pair<String, String>>
        get() = _initUser
    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar

    private var currentMessages = mutableListOf<ChatItems<Message>>()

    private var currentUser: User? = null
    private var chatWithUserId: String? = null
    private var roomId: String? = null
    private var localRoom: LocalRoom? = null

    fun initCurrentUser(user: User) {
        this.currentUser = user
    }

    fun initRoom(localRoom: LocalRoom) {
        this.localRoom = localRoom
            roomId = localRoom.roomId
            _initUser.value = Pair(localRoom.chatWithName, localRoom.chatWithImageUrl)
            chatWithUserId = localRoom.chatWithId
        getRoomUseCase.roomExists(localRoom.roomId).onEach {
            when(it){
                is Resource.Loading ->{
                    _progressBar.postValue(View.VISIBLE)
                }
                is Resource.Success ->{
                    if(it.data!!) {
                        getAndSortMessages()
                        roomListener()
                    }else{
                        databaseRepository.createRoom(
                            Room(
                                HashMap(),
                                localRoom.roomId,
                                localRoom.chatWithId,
                                localRoom.chatWithName,
                                currentUser!!.id,
                                currentUser!!.fullName,
                            )
                        )
                        roomListener()
                        _progressBar.postValue(View.GONE)
                    }
                }
                is Resource.Error ->{
                    _progressBar.postValue(View.GONE)
                    Log.d("debug", it.msg.toString())}
            }
        }.launchIn(viewModelScope)

    }

    fun sendMessage(str: String) {
        val currentUser = currentUser!!

        val msg = Message(
            id = UUID.randomUUID().toString(),
            text = str,
            timestamp = Date().time,
            userId = currentUser.id,
            userName = currentUser.fullName,
            imageUrl = currentUser.imagesUrl.getUserImage(),
        )
        if (currentMessages.isNotEmpty() && currentMessages[0].data?.timestamp?.toDate() != msg.timestamp.toDate()) {
            currentMessages.add(0, ChatItems.Other(msg))
        }
        currentMessages.add(0, ChatItems.UserRight(msg))
        _onMessageAdded.postValue(currentMessages)

        pushMessageToDatabase(roomId!!, msg)
    }

    private fun roomListener() = viewModelScope.launch {
        val liveData = roomListenerUseCase.roomListener(currentUser!!.id, roomId!!)
        liveData.observeForever {
            getAndSortMessages()
        }
    }

    private fun getAndSortMessages() {
        getMessagesUseCase.getMessages(roomId = roomId!!).onEach { item->
            when(item){
                is Resource.Success ->{
                    currentMessages.clear()
                    item.data?.forEach {
                        it.value.forEach { item ->
                            val chatItem = if (item.userId == currentUser!!.id) {
                                ChatItems.UserRight(item)
                            } else {
                                ChatItems.UserLeft(item)
                            }
                            currentMessages.add(chatItem)
                        }
                        currentMessages.add(
                            ChatItems.Other(
                                Message(timestamp = it.value.first().timestamp)
                            )
                        )
                    }
                    _progressBar.postValue(View.GONE)
                    _onMessageAdded.postValue(currentMessages)
                }
                is Resource.Error -> {
                    _progressBar.postValue(View.GONE)
                }
                is Resource.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }


    private fun pushMessageToDatabase(roomId: String, msg: Message) = viewModelScope.launch {
        databaseRepository.pushMessage(roomId, msg)
        databaseRepository.pushRoomIdToUser(roomId, chatWithUserId!!)
        databaseRepository.pushRoomIdToUser(roomId, currentUser!!.id)
    }

}