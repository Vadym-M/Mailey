package com.devx.mailey.data.firebase

import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.ResultState
import kotlinx.coroutines.flow.Flow

interface DatabaseService {
    suspend fun getRoomById(roomId: String): ResultState<Room>
    fun createRoom(room: Room): Boolean
    fun writeMessage(nameUser: String, message: Message, room: Room): Boolean
}