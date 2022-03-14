package com.devx.mailey.data.firebase

import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User

interface DatabaseService {
    suspend fun getRooms()
    fun writeMessage(user: User, message: Message, room: Room)
}