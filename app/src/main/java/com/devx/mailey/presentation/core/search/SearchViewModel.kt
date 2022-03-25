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


    private val _onRoomCreated = MutableLiveData<Event<Pair<String, User>>>()
    val onRoomCreated: LiveData<Event<Pair<String, User>>>
        get() = _onRoomCreated


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

    fun createRoomId(leftUser: User, rightUserId: String){
        val leftId = leftUser.id.map { if(!it.isLetter()) it else ""}.joinToString("").toLong()
        val rightId = rightUserId.map { if(!it.isLetter()) it else ""}.joinToString("").toLong()
        if(leftId < rightId){
            _onRoomCreated.postValue(Event(Pair(leftUser.id.take(14) + rightUserId.take(14), leftUser)))
        }else{
            _onRoomCreated.postValue(Event(Pair(rightUserId.take(14) + leftUser.id.take(14), leftUser)))
        }

}}