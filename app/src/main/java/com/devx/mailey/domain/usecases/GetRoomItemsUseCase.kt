package com.devx.mailey.domain.usecases

import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import com.devx.mailey.util.getUserImage
import kotlinx.coroutines.*
import javax.inject.Inject

class GetRoomItemsUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {


    suspend fun getRoomItems(user: User): List<RoomItem> {
        databaseRepository.getRooms().let {
            val roomsLocal = databaseRepository.getRooms()
            if (roomsLocal.isNotEmpty()) {
                return mapToRoomItems(roomsLocal, user)
            } else {
                return withContext(Dispatchers.IO) {
                    val roomsNetwork =
                        user.rooms?.map { async { databaseRepository.getRoomById(it.key) } }
                            ?.awaitAll()
                    return@withContext mapToRoomItems(roomsNetwork ?: emptyList(), user)

                }
            }
        }
    }

    private suspend fun getUserUrl(userId: String): String {
        return withContext(Dispatchers.Default) {
            databaseRepository.getUserById(userId).imagesUrl.getUserImage()
        }
    }

    private suspend fun mapToRoomItems(rooms: List<Room>, user: User): List<RoomItem> {
        val newList = mutableListOf<RoomItem>()
        for (i in rooms.indices) {
            if (rooms[i].firstUserId != user.id) {
                newList.add(createRoomItemWithFirstUser(rooms[i]))
            } else {
                newList.add(createRoomItemWithSecondUser(rooms[i]))
            }

        }
        return newList
    }

    private suspend fun createRoomItemWithFirstUser(room: Room): RoomItem {
        val url = getUserUrl(room.firstUserId)
        return RoomItem(
            room.firstUserId,
            room.roomId,
            url,
            room.firstUserName,
            room.messages.getLastMessage(),
            room.messages.getLastMessageTimestamp()
        )
    }

    private suspend fun createRoomItemWithSecondUser(room: Room): RoomItem {
        val url = getUserUrl(room.secondUserId)
        return RoomItem(
            room.secondUserId,
            room.roomId,
            url,
            room.secondUserName,
            room.messages.getLastMessage(),
            room.messages.getLastMessageTimestamp()
        )
    }

}