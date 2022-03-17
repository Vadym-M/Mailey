package com.devx.mailey.presentation.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) : ViewModel(){

    //val userData
    init {
        Log.d("viewModel", databaseRepository.hashCode().toString())
    }

    private fun getCurrentUserData() = viewModelScope.launch{
        databaseRepository.getCurrentUserData()
    }
}