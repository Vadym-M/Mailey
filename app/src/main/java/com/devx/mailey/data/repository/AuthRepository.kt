package com.devx.mailey.data.repository

import android.util.Log
import com.devx.mailey.data.firebase.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService){
    suspend fun register(fullName: String, email:String, password:String) = authService.register(fullName, email, password)
    suspend fun login(email: String, password: String) = authService.login(email, password)
    suspend fun getUser() = authService.getUser()
    fun signOut() = authService.signOut()
}