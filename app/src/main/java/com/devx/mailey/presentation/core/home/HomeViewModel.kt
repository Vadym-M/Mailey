package com.devx.mailey.presentation.core.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.Constants
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import com.devx.mailey.util.getUserImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
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
        val newList = mutableListOf<RoomItem>()

        if (result.isNotEmpty()) {
            for(count in result.indices) {
                if (result[count].firstUserId != user.id) {
                    val url =
                        withContext(Dispatchers.Default) {
                            databaseRepository.getUserById(result[count].firstUserId).imagesUrl.getUserImage()
                        }

                    newList.add(
                        RoomItem(
                            result[count].firstUserId,
                            result[count].roomId,
                            url,
                            result[count].firstUserName,
                            result[count].messages.getLastMessage(),
                            result[count].messages.getLastMessageTimestamp()
                        )
                    )
                } else {
                    val url =
                        withContext(Dispatchers.Default) {
                            databaseRepository.getUserById(result[count].secondUserId).imagesUrl.getUserImage()
                        }

                    newList.add(
                        RoomItem(
                            result[count].secondUserId,
                            result[count].roomId,
                            url,
                            result[count].secondUserName,
                            result[count].messages.getLastMessage(),
                            result[count].messages.getLastMessageTimestamp()
                        )
                    )
                }

            }
            _rooms.postValue(newList)
            _progressBar.value = View.GONE
        } else {
            try {
                val response =
                    user.rooms?.map { async { databaseRepository.getRoomById(it.key) } }
                        ?.awaitAll()
                response?.forEach {
                    if (it.firstUserId != user.id) {
                        val url =
                            withContext(Dispatchers.Default) {
                                databaseRepository.getUserById(it.firstUserId).imagesUrl.getUserImage()
                            }
                        newList.add(
                            RoomItem(
                                it.firstUserId,
                                it.roomId,
                                url,
                                it.firstUserName,
                                it.messages.getLastMessage(),
                                it.messages.getLastMessageTimestamp()
                            )
                        )
                    } else {
                        val url =
                            withContext(Dispatchers.Default) {
                                databaseRepository.getUserById(it.secondUserId).imagesUrl.getUserImage()
                            }
                        newList.add(
                            RoomItem(
                                it.secondUserId,
                                it.roomId,
                                url,
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