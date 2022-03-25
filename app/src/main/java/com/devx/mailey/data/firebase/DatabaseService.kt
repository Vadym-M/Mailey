package com.devx.mailey.data.firebase

import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.ResultState
import kotlinx.coroutines.flow.Flow

interface DatabaseService {
    suspend fun getRooms()
    fun writeMessage(user: User, message: Message, room: Room)
    suspend fun getCurrentUserData(): User?
    suspend fun updateImagesUrl(urls: List<String>)
    suspend fun getRoomById(roomId: String): Room
    suspend fun createRoom(room: Room): Boolean
    fun writeMessage(nameUser: String, message: Message, room: Room): Boolean
    suspend fun searchUserByName(str:String): Flow<ResultState<List<User>>>
    suspend fun pushRoomIdToUser(roomId: String, userId:String)
    suspend fun isRoomExist(roomId: String):Boolean
    suspend fun pushMessage(roomId: String, msg: Message)
    fun addMessageListener(liveData: MutableLiveData<MutableMap<String, Message>>, roomId: String)
}