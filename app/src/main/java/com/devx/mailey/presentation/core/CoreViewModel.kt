package com.devx.mailey.presentation.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {
    private var currentUser: User? = null
    private var userJob: Job? = null

    private var string: String? = null

    private var chatPair: Pair<String ,User>? = null


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _fragment = MutableLiveData<Fragment?>()
    val onFragmentChanged: LiveData<Fragment?>
        get() = _fragment


    fun getCurrentUser():User{
        return user.value!!
    }
    fun fetchCurrentUser() {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            val user = databaseRepository.getCurrentUserData()
            _user.postValue(user)
            currentUser = user
        }
    }

    fun setFragment(fragment: Fragment?){
        _fragment.value = fragment
    }

    fun putString(str:String){
        string = str
    }
    fun getString(): String?{
        return string
    }
    fun putChatPair(pair: Pair<String, User>){
        chatPair = pair
    }
    fun getChatPair(): Pair<String, User>?{
        return chatPair
    }

}