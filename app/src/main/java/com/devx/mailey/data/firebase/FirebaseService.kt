package com.devx.mailey.data.firebase

import android.net.Uri
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.presentation.auth.AuthState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseService {

//    auth functions
    suspend fun  register(fullName: String, email:String, password:String): Flow<AuthState<AuthResult>>
    suspend fun  login(email:String, password:String): Flow<AuthState<AuthResult>>
    fun  signOut(): Boolean

    //    get from Db
    suspend fun getUser(): FirebaseUser?
    suspend fun getRooms(): List<String>

//    save to Db
    fun writeMessage(user: User, message: Message, room: Room)
    fun loadImage(uri: Uri)
}