package com.devx.mailey.presentation.core.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devx.mailey.data.repository.AuthRepository
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun signOut() {
        _onSignOut.value = authRepository.signOut()
    }

    fun loadImage(uri: Uri){
        storageRepository.loadImage(uri)
    }
}