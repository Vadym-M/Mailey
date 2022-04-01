package com.devx.mailey.presentation.core.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    private val _rooms = MutableLiveData<List<RoomItem>>()
    val rooms: LiveData<List<RoomItem>>
        get() = _rooms

    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar

    fun getUserRooms(user: User) = viewModelScope.launch {
        val result = databaseRepository.getRooms()
        if (result.isNotEmpty()) {
            val newList = mutableListOf<RoomItem>()
            result.forEach {
                if (it.firstUserId != user.id) {
                    newList.add(
                        RoomItem(
                            it.firstUserId,
                            it.roomId,
                            it.firstUserUrl,
                            it.firstUserName,
                            it.messages.getLastMessage(),
                            it.messages.getLastMessageTimestamp()
                        )
                    )
                } else {
                    newList.add(
                        RoomItem(
                            it.secondUserId,
                            it.roomId,
                            it.secondUserUrl,
                            it.secondUserName,
                            it.messages.getLastMessage(),
                            it.messages.getLastMessageTimestamp()
                        )
                    )
                }

            }
            _rooms.postValue(newList)
            _progressBar.value = View.GONE
        } else {
            try {
                val response =
                    user.rooms?.map { async { databaseRepository.getRoomById(it.key) } }?.awaitAll()
                val newList = mutableListOf<RoomItem>()
                response?.forEach {
                    if (it.firstUserId != user.id) {
                        newList.add(
                            RoomItem(
                                it.firstUserId,
                                it.roomId,
                                it.firstUserUrl,
                                it.firstUserName,
                                it.messages.getLastMessage(),
                                it.messages.getLastMessageTimestamp()
                            )
                        )
                    } else {
                        newList.add(
                            RoomItem(
                                it.secondUserId,
                                it.roomId,
                                it.secondUserUrl,
                                it.secondUserName,
                                it.messages.getLastMessage(),
                                it.messages.getLastMessageTimestamp()
                            )
                        )
                    }

                }
                _rooms.postValue(newList)
                _progressBar.value = View.GONE
            } catch (e: Exception) {
                _rooms.postValue(emptyList())
                _progressBar.value = View.GONE
            }
        }
    }
}