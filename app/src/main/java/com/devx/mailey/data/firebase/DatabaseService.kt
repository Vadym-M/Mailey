package com.devx.mailey.data.firebase

import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.ResultState

interface DatabaseService {
    suspend fun getRooms()
    fun writeMessage(user: User, message: Message, room: Room)
    suspend fun getCurrentUserData(): User?
    suspend fun updateImagesUrl(urls: List<String>)
    suspend fun getRoomById(roomId: String): ResultState<Room>
    fun createRoom(room: Room): Boolean
    fun writeMessage(nameUser: String, message: Message, room: Room): Boolean
}