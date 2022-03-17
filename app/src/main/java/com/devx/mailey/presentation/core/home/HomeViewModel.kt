package com.devx.mailey.presentation.core.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val databaseRepository: DatabaseRepository): ViewModel() {
    init {
        getRoomById()
    }
    fun createRoom() {
        val message = Message("text", "vadyum")
        val message2 = Message("text2", "vadym2")
        val room = Room(listOf(message, message2), "test", listOf("1"))
        databaseRepository.createRoom(room)
    }

    private fun getRoomById() = viewModelScope.launch {
        val result = databaseRepository.getRoomById("test")
        Log.d("vadim", result.result.toString())
    }
}