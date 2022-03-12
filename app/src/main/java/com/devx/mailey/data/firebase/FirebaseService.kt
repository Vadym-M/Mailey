package com.devx.mailey.data.firebase

import com.devx.mailey.presentation.auth.AuthState
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface FirebaseService {

    suspend fun  register(fullName: String, email:String, password:String): Flow<AuthState<AuthResult>>

}