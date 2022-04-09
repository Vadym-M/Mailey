package com.devx.mailey.presentation.core.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.domain.usecases.GetRoomItemsUseCase
import com.devx.mailey.util.getLastMessage
import com.devx.mailey.util.getLastMessageTimestamp
import com.devx.mailey.util.getUserImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getRoomItemsUseCase: GetRoomItemsUseCase) :
    ViewModel() {

    private val _rooms = MutableLiveData<List<RoomItem>>()
    val rooms: LiveData<List<RoomItem>>
        get() = _rooms

    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar

    fun getUserRooms(user: User) = viewModelScope.launch {
       val res = getRoomItemsUseCase.getRoomItems(user)
        _rooms.postValue(res)
        _progressBar.postValue(View.GONE)
    }
}