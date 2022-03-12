package com.devx.mailey.presentation.auth

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.repository.FirebaseRepository
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    private val _authState = MutableLiveData<AuthState<AuthResult>>()
    val authState: LiveData<AuthState<AuthResult>>
        get() = _authState

    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch{
        firebaseRepository.register(fullName, email, password).collect { result ->
            _authState.postValue(result)
//            when (result) {
//                is AuthState.Success -> {
//                    _toastMsg.postValue(result.result.toString())
//                    _progressBar.postValue(View.GONE)
//                }
//                is AuthState.Error -> {
//                    _toastMsg.postValue(result.msg.toString())
//                    _progressBar.postValue(View.GONE)
//                }
//                is AuthState.Loading -> {
//                    _progressBar.postValue(View.VISIBLE)
//                }
 //       }

        }
    }
}