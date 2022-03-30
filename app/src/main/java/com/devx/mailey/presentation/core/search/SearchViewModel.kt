package com.devx.mailey.presentation.core.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.LocalRoom
import com.devx.mailey.util.Event
import com.devx.mailey.util.ResultState
import com.devx.mailey.util.getUserImage
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


    private val _onRoomIdCreated = MutableLiveData<Event<LocalRoom>>()
    val onLocalRoomIdCreated: LiveData<Event<LocalRoom>>
        get() = _onRoomIdCreated


    fun searchUserByName(str: String) {
        if (str.isNotBlank()) {
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

    fun createRoomId(userChatWith: User, currentUserId: String) {
        val firstId =
            userChatWith.id.map { if (!it.isLetter()) it else "" }.joinToString("").toLong()
        val secondId =
            currentUserId.map { if (!it.isLetter()) it else "" }.joinToString("").toLong()

        val roomId = if (firstId < secondId) {
            userChatWith.id.take(14) + currentUserId.take(14)
        } else {
            currentUserId.take(14) + userChatWith.id.take(14)
        }
        val roomData = LocalRoom(
            roomId,
            userChatWith.fullName,
            userChatWith.id,
            userChatWith.imagesUrl.getUserImage()
        )
        _onRoomIdCreated.postValue(Event(roomData))
    }}