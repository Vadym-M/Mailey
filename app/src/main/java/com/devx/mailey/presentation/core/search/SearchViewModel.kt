package com.devx.mailey.presentation.core.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.Constants
import com.devx.mailey.util.Event
import com.devx.mailey.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository) :ViewModel(){

    private var searchJob: Job? = null

    private val _searchedUsers = MutableLiveData<ResultState<List<User>>>()
    val searchedUsers: LiveData<ResultState<List<User>>>
    get() = _searchedUsers

    private val _currentUser = MutableLiveData<User>()
    private val currentUser: LiveData<User>
        get() = _currentUser

    private val _onRoomCreated = MutableLiveData<Event<Pair<String, String>>>()
    val onRoomCreated: LiveData<Event<Pair<String, String>>>
        get() = _onRoomCreated

    fun searchUserByName(str: String) {
        if (str.isNotBlank() && currentUser.value != null) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                databaseRepository.searchUserByName(str).collect {
                    _searchedUsers.postValue(it)
                }
            }
        } else {
            searchJob?.cancel()
            _searchedUsers.postValue(ResultState.Success(emptyList()))
        }
    }
    fun getCurrentUser() = viewModelScope.launch{
        val user = databaseRepository.getCurrentUserData()
            Log.d("user", user.rooms?.values.toString())
        _currentUser.postValue(user)
    }

    fun createRoomId(leftUser: User){
        val leftId = leftUser.id.map { if(!it.isLetter()) it else ""}.joinToString("").toLong()
        val rightUser = currentUser.value!!
        val rightId = rightUser.id.map { if(!it.isLetter()) it else ""}.joinToString("").toLong()
        if(leftId < rightId){
            _onRoomCreated.postValue(Event(Pair(leftUser.id.take(14) + rightUser.id.take(14), leftUser.id)))
        }else{
            _onRoomCreated.postValue(Event(Pair(rightUser.id.take(14) + leftUser.id.take(14), leftUser.id)))
        }
//        viewModelScope.launch {
//            val rightUser = currentUser.value!!
//            val roomId = leftUser.id.take(14) + rightUser.id.take(14)
//            val otherId = rightUser.id.take(14) + leftUser.id.take(14)
//            val res1 = async { databaseRepository.isRoomExist(roomId) }.await()
//            val res2 = async { databaseRepository.isRoomExist(otherId) }.await()
//
//            if(res1){
//
//               if(rightUser.rooms.isNullOrEmpty() || !rightUser.rooms.values.contains(roomId)){
//                   async { databaseRepository.pushRoomIdToUser(roomId, rightUser.id) }
//               }
//            }else if(res2){
//
//                if(rightUser.rooms.isNullOrEmpty() || !rightUser.rooms.values.contains(otherId)){
//                    async { databaseRepository.pushRoomIdToUser(otherId, rightUser.id) }
//                }
//            }else{
//
//                val room = Room(HashMap(), roomId, leftUser.id, rightUser.id)
//                async { databaseRepository.createRoom(room)}
//                async { databaseRepository.pushRoomIdToUser(roomId, rightUser.id) }
//
//            }
//        }
    }
//    private suspend fun getRoom(roomId:String)= viewModelScope.launch {
//        val result = databaseRepository.getRoomById(roomId)
//        _onRoomCreated.postValue(Event(result))
//    }
}