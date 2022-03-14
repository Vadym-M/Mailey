package com.devx.mailey.presentation.core.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx.mailey.data.repository.AuthRepository
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.data.repository.StorageRepository
import com.devx.mailey.util.ResultState
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
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

    private val _onLoadImage =MutableLiveData<ResultState<StorageReference>>()
    val onLoadImage: LiveData<ResultState<StorageReference>>
    get() = _onLoadImage

    fun signOut() {
        _onSignOut.value = authRepository.signOut()
    }

    fun loadImage(uri: Uri?) = viewModelScope.launch {
        storageRepository.loadImage(uri!!).collect { result -> _onLoadImage.postValue(result) }
    }

}