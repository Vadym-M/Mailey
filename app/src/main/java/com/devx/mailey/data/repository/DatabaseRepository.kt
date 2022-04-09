package com.devx.mailey.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.FirebaseConstants.ABOUT
import com.devx.mailey.util.FirebaseConstants.FULL_NAME
import com.devx.mailey.util.FirebaseConstants.MOBILE_PHONE
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val databaseService: DatabaseService) :
    DatabaseService {

    private val userMutex = Mutex()
    private var currentUser: User? = null
    private val roomList: MutableList<Room> = mutableListOf()

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

    override suspend fun pushRoomIdToUser(roomId: String, userId: String) =
        databaseService.pushRoomIdToUser(roomId, userId)

    override suspend fun getRoomById(roomId: String): Room {
        val room = databaseService.getRoomById(roomId)
        if (roomList.isNotEmpty()) {
            roomList.forEachIndexed { index, item ->
                if (item.roomId == room.roomId) {
                    roomList[index] = room
                }
            }
            if (!roomList.contains(room)) {
                roomList.add(room)
            }
        } else {
            roomList.add(room)
        }
        return room
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

    fun getRooms(): MutableList<Room> {

        roomList.sortWith(compareBy {
            val test = it.messages.values.toMutableList()
            test.sortWith(compareBy { i -> i.timestamp })
            test.last().timestamp
        })
        roomList.reverse()
        return roomList
    }

    fun getRoomFromCache(roomId: String): Room? {
        return roomList.find { it.roomId == roomId }
    }
}