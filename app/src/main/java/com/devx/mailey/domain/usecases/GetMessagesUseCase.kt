package com.devx.mailey.domain.usecases

import android.util.Log
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.ChatItems
import com.devx.mailey.util.toDate
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {
    suspend fun getMessages(roomId: String):Map<String, List<Message>>{
        val room = databaseRepository.getRoomById(roomId)
        val messages = room.messages.values.toMutableList()
        messages.sortWith( compareBy { it.timestamp })
        messages.reverse()
        Log.d("debug", messages.groupBy { it.timestamp.toDate() }.toString())
        return messages.groupBy { it.timestamp.toDate() }
    }
}