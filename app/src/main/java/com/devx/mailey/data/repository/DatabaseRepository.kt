package com.devx.mailey.data.repository

import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val databaseService: DatabaseService) {

    private val userMutex = Mutex()
    private var currentUser: User? = null
    private val roomList: MutableList<Room> = mutableListOf()
    suspend fun getCurrentUserData(): User {
        if (currentUser == null) {
            val user = databaseService.getCurrentUserData()
            userMutex.withLock {
                this.currentUser = user
            }
        }
        return userMutex.withLock { this.currentUser!! }
    }

    suspend fun addImageUrl(url: String) {
        val urls = currentUser?.imagesUrl
        val res = if (urls?.add(url) == true) urls else listOf(url)
        databaseService.updateImagesUrl(res)
    }

    suspend fun searchUserByName(str: String) = databaseService.searchUserByName(str)
    suspend fun createRoom(room: Room) = databaseService.createRoom(room)
    suspend fun pushRoomIdToUser(roomId: String, userId: String) =
        databaseService.pushRoomIdToUser(roomId, userId)

    suspend fun getRoomById(roomId: String): Room {
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

    suspend fun pushMessage(roomId: String, msg: Message) = databaseService.pushMessage(roomId, msg)
    fun addMessageListener(liveData: MutableLiveData<MutableMap<String, Message>>, roomId: String) =
        databaseService.addMessageListener(liveData, roomId)

    suspend fun onRoomsChanged(liveData: MutableLiveData<String>, userId: String) =
        databaseService.onRoomsChanged(liveData, userId)

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