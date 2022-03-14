package com.devx.mailey.data.repository

import com.devx.mailey.data.firebase.AuthService
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.model.Room
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val databaseService: DatabaseService) {
    fun createRoom(room: Room) = databaseService.createRoom(room)
    suspend fun getRoomById(roomId: String) = databaseService.getRoomById(roomId)
}