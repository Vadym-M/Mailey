package com.devx.mailey.presentation.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.repository.AuthRepository
import com.devx.mailey.util.ResultState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _authState = MutableLiveData<ResultState<AuthResult>>()
    val resultState: LiveData<ResultState<AuthResult>>
        get() = _authState

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?>
    get() = _user

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch{
        authRepository.register(fullName, email, password).collect { result ->
            _authState.postValue(result)
        }
    }
    fun login(email: String, password: String) = viewModelScope.launch {
        authRepository.login(email, password).collect{ result ->
            _authState.postValue(result)
        }
    }
    fun getUser() = viewModelScope.launch {
        delay(1000)
        val user = authRepository.getUser()
        _user.postValue(user)
    }


}