package com.devx.mailey.domain.usecases

import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.NetworkResult
import com.devx.mailey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRoomUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {
    fun roomExists(roomId: String): Flow<Resource<Boolean>> = flow{
        emit(Resource.Loading(null))
        when(val result = databaseRepository.roomExists(roomId)){
            is NetworkResult.Success ->{
                emit(Resource.Success(result.result))
            }
            is NetworkResult.Error ->{
                emit(Resource.Error(result.msg))
            }
        }
    }
}