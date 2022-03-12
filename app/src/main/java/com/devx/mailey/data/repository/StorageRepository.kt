package com.devx.mailey.data.repository

import android.net.Uri
import com.devx.mailey.data.firebase.FirebaseService
import javax.inject.Inject

class StorageRepository @Inject constructor(private val firebaseService: FirebaseService) {
    fun loadImage(uri: Uri) = firebaseService.loadImage(uri)
}