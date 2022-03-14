package com.devx.mailey.data.firebase

import android.net.Uri

interface StorageService {
    fun loadImage(uri: Uri)
}