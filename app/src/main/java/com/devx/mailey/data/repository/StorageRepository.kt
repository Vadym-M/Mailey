package com.devx.mailey.data.repository

import android.net.Uri
import android.util.Log
import com.devx.mailey.data.firebase.StorageService
import javax.inject.Inject

class StorageRepository @Inject constructor(private val firebaseService: StorageService) {
    fun loadImage(uri: Uri) = firebaseService.loadImage(uri)
}