package com.devx.mailey.presentation.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devx.mailey.data.model.User


class CoreViewModel: ViewModel(){
    private val _users = MutableLiveData<Map<Int,User>>()
    val users : LiveData<Map<Int,User>>
    get() = _users

    fun setUsersForChat(users: Map<Int,User>){
        _users.postValue(users)
    }

}