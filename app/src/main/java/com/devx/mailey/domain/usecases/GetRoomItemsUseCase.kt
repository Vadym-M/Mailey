package com.devx.mailey.domain.usecases

import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.Resource
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import com.devx.mailey.util.getUserImage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRoomItemsUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {


    fun getRoomItems(user: User): Flow<Resource<List<RoomItem>>> = flow {
        emit(Resource.Loading(null))
        try {
            val rooms = withContext(Dispatchers.IO) {
                return@withContext user.rooms?.map { async { databaseRepository.getRoomById(it.key).result!! } }
                    ?.awaitAll()?.toMutableList()

            }
            rooms?.sortWith(compareBy {
                val test = it.messages.values.toMutableList()
                test.sortWith(compareBy { i -> i.timestamp })
                test.last().timestamp
            })
            rooms?.reverse()
            emit(Resource.Success(mapToRoomItems((rooms ?: emptyList()), user)))
        }catch (e:Exception){
            emit(Resource.Error(e.message))
        }

    }

    private suspend fun getUserUrl(userId: String): String {
        return withContext(Dispatchers.Default) {
            databaseRepository.getUserById(userId).imagesUrl.getUserImage()
        }
    }

    private suspend fun mapToRoomItems(rooms: List<Room?>, user: User): List<RoomItem> {
        val newList = mutableListOf<RoomItem>()
        for (i in rooms.indices) {
            if (rooms[i]?.firstUserId != user.id) {
                newList.add(createRoomItemWithFirstUser(rooms[i]!!))
            } else {
                newList.add(createRoomItemWithSecondUser(rooms[i]!!))
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