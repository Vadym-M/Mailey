package com.devx.mailey.data.firebase

import android.net.Uri
import com.devx.mailey.util.ResultState
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface StorageService {
    suspend fun loadImage(uri: Uri): Flow<ResultState<StorageReference>>
}