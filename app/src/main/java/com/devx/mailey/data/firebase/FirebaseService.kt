package com.devx.mailey.data.firebase

import android.net.Uri
import com.devx.mailey.presentation.auth.AuthState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseService {

//    auth functions
    suspend fun  register(fullName: String, email:String, password:String): Flow<AuthState<AuthResult>>
    suspend fun  login(email:String, password:String): Flow<AuthState<AuthResult>>

//    user functions
    suspend fun getUser(): FirebaseUser?
    fun  signOut(): Boolean
    suspend fun getRooms()


//    messages
    fun writeMessage()
    fun loadImage(uri: Uri)
}