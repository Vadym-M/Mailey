package com.devx.mailey.presentation.core.profile

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.AuthRepository
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.data.repository.StorageRepository
import com.devx.mailey.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _onSignOut = MutableLiveData<Boolean>()
    val onSignOut: LiveData<Boolean>
        get() = _onSignOut

    private val _onLoadImage = MutableLiveData<ResultState<String>>()
    val onLoadImage: LiveData<ResultState<String>>
        get() = _onLoadImage

    fun signOut() {
        _onSignOut.value = authRepository.signOut()
    }

    fun loadImage(uri: Uri?) = viewModelScope.launch {
        uri?.let { it ->
            storageRepository.loadImage(it).collect { state ->
                _onLoadImage.postValue(state as ResultState<String>)
            }
        }
    }

    fun addImageToUser(url:String) = viewModelScope.launch{
        databaseRepository.addImageUrl(url)
    }


}