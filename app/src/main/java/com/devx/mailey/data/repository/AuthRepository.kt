package com.devx.mailey.data.repository

import com.devx.mailey.data.firebase.FirebaseService
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseService: FirebaseService){
    suspend fun register(fullName: String, email:String, password:String) = firebaseService.register(fullName, email, password)
    suspend fun login(email: String, password: String) = firebaseService.login(email, password)
    suspend fun getUser() = firebaseService.getUser()
    fun signOut() = firebaseService.signOut()
}