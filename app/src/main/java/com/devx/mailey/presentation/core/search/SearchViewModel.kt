package com.devx.mailey.presentation.core.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :ViewModel(){

    private var searchJob: Job? = null

    private val _searchedUsers = MutableLiveData<ResultState<List<User>>>()
    val searchedUsers: LiveData<ResultState<List<User>>>
    get() = _searchedUsers

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
}