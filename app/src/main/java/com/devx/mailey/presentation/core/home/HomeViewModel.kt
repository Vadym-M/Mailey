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
    }

    private fun getRoomById() = viewModelScope.launch {
    }
}