package com.devx.mailey.data.firebase

import android.net.Uri
import com.devx.mailey.util.NetworkResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface StorageService {
    suspend fun loadImage(uri: Uri): Flow<NetworkResult<String>>
    suspend fun deleteImage(ref:StorageReference)
}