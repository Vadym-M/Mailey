package com.devx.mailey.domain.usecases

import androidx.lifecycle.LiveData
import com.devx.mailey.data.repository.DatabaseRepository
import javax.inject.Inject

class RoomListenerUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {
suspend fun roomListener(userId: String, roomId:String): LiveData<String> {
    return databaseRepository.onRoomChanged(userId, roomId)
}
}