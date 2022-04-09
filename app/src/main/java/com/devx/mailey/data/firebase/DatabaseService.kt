package com.devx.mailey.data.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.ResultState
import kotlinx.coroutines.flow.Flow

interface DatabaseService {
    suspend fun getCurrentUserData(): User?

    suspend fun updateImagesUrl(urls: List<String>)

    suspend fun getUserById(id:String):User

    suspend fun getRoomById(roomId: String): Room

    suspend fun createRoom(room: Room): Boolean

    suspend fun searchUserByName(str: String): Flow<ResultState<List<User>>>

    suspend fun pushRoomIdToUser(roomId: String, userId: String)

    suspend fun pushMessage(roomId: String, msg: Message)

    fun addMessageListener(liveData: MutableLiveData<MutableMap<String, Message>>, roomId: String)

    suspend fun onRoomsChanged(userId: String): LiveData<String>
    suspend fun onRoomChanged(userId: String, roomId: String): LiveData<String>

//    suspend fun pushRoomChanged(userId: String, roomId: String)

    fun changeUserFullName(value: String)

    fun changeUserAbout(value: String)

    fun changeUserMobilePhone(value: String)
}