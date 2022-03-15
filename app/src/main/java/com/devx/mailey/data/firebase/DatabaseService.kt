package com.devx.mailey.data.firebase

import android.net.Uri
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.google.firebase.storage.StorageReference

interface DatabaseService {
    suspend fun getRooms()
    fun writeMessage(user: User, message: Message, room: Room)
    suspend fun getCurrentUserData(): User?
    suspend fun updateImagesUrl(urls: List<String>)
}