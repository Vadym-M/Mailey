package com.devx.mailey.domain.usecases

import com.devx.mailey.data.model.Message
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.Resource
import com.devx.mailey.util.isSuccessful
import com.devx.mailey.util.toDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {
    fun getMessages(roomId: String): Flow<Resource<Map<String, List<Message>>>> =
        flow {
            emit(Resource.Loading(null))
            try {
                val room = databaseRepository.getRoomById(roomId)
                if (room.isSuccessful()){
                    val messages = room.result?.messages?.values?.toMutableList()
                    messages?.sortWith(compareBy { it.timestamp })
                    messages?.reverse()
                    emit(Resource.Success(messages?.groupBy { it.timestamp.toDate() }))
                }else{
                    emit(Resource.Error("room doesn't exist"))
                }

            }catch (e:Exception){
                emit(Resource.Error(e.message))
            }
        }

}
