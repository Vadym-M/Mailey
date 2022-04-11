package com.devx.mailey.data.repository

import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val databaseService: DatabaseService) :
    DatabaseService {

    private val userMutex = Mutex()
    private var currentUser: User? = null

    override suspend fun getCurrentUserData(): User {
        if (currentUser == null) {
            val user = databaseService.getCurrentUserData()
            userMutex.withLock {
                this.currentUser = user
            }
        }
        return userMutex.withLock { this.currentUser!! }
    }

    override suspend fun updateImagesUrl(urls: List<String>) {
        val urlsList = currentUser?.imagesUrl
        databaseService.updateImagesUrl(urlsList?.plus(urls) ?: emptyList())
    }

    override suspend fun getUserById(id: String) = databaseService.getUserById(id)

    override suspend fun searchUserByName(str: String) = databaseService.searchUserByName(str)

    override suspend fun createRoom(room: Room) = databaseService.createRoom(room)

    override suspend fun pushRoomIdToUser(roomId: String, userId: String) {
        databaseService.pushRoomIdToUser(roomId, userId)
    }


    override suspend fun getRoomById(roomId: String): NetworkResult<Room> {
       return databaseService.getRoomById(roomId)
    }

    override suspend fun pushMessage(roomId: String, msg: Message) =
        databaseService.pushMessage(roomId, msg)

    override fun addMessageListener(
        liveData: MutableLiveData<MutableMap<String, Message>>,
        roomId: String
    ) =
        databaseService.addMessageListener(liveData, roomId)

    override suspend fun onRoomsChanged(userId: String) =
        databaseService.onRoomsChanged(userId)

    override suspend fun onRoomChanged(userId: String, roomId: String) = databaseService.onRoomChanged(userId, roomId)
    override suspend fun roomExists(roomId: String) = databaseService.roomExists(roomId)

    override fun changeUserFullName(value: String) {
        currentUser?.fullName = value
        databaseService.changeUserFullName(value)
    }

    override fun changeUserAbout(value: String) {
        currentUser?.about = value
        databaseService.changeUserAbout(value)
    }

    override fun changeUserMobilePhone(value: String) {
        currentUser?.mobilePhone = value
        databaseService.changeUserMobilePhone(value)
    }
}