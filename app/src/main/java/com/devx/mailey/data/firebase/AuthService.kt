package com.devx.mailey.data.firebase
import com.devx.mailey.util.NetworkResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun  register(fullName: String, email:String, password:String): Flow<NetworkResult<AuthResult>>
    suspend fun  login(email:String, password:String): Flow<NetworkResult<AuthResult>>
    fun signOut(): Boolean
    suspend fun getUser(): FirebaseUser?
}