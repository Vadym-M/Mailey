package com.devx.mailey.data.repository

import com.devx.mailey.data.firebase.FirebaseService
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val firebaseService: FirebaseService){
    suspend fun register(fullName: String, email:String, password:String) = firebaseService.register(fullName, email, password)
}