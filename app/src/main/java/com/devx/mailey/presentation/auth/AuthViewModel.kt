package com.devx.mailey.presentation.auth

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.repository.FirebaseRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState<AuthResult>>()
    val authState: LiveData<AuthState<AuthResult>>
        get() = _authState

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?>
    get() = _user

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch{
        firebaseRepository.register(fullName, email, password).collect { result ->
            _authState.postValue(result)
        }
    }
    fun login(email: String, password: String) = viewModelScope.launch {
        firebaseRepository.login(email, password).collect{ result ->
            _authState.postValue(result)
        }
    }
    fun getUser() = viewModelScope.launch {
        delay(2000)
        val user = firebaseRepository.getUser()
        _user.postValue(user)
    }
}