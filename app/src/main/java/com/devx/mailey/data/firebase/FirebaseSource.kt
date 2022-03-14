package com.devx.mailey.data.firebase

import android.net.Uri
import android.util.Log
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.ResultState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirebaseSource : AuthService, StorageService, DatabaseService {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val database: DatabaseReference by lazy { Firebase.database.reference }
    private val storageRef: StorageReference by lazy { Firebase.storage.reference }
    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Flow<ResultState<AuthResult>> = flow {

        emit(ResultState.Loading(null))
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid.let { id ->
                val user = User(fullName = fullName, email = email, id = id.toString(), null)
                database.child("users").child(id.toString()).setValue(user)
            }.await()
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    override suspend fun login(email: String, password: String): Flow<ResultState<AuthResult>> =
        flow {
            emit(ResultState.Loading(null))
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(ResultState.Success(result))
            } catch (e: Exception) {
                emit(ResultState.Error(e.message))
            }
        }

    override suspend fun getUser(): FirebaseUser? = firebaseAuth.currentUser
    override fun signOut(): Boolean {
        return try {
            firebaseAuth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loadImage(uri: Uri): Flow<ResultState<StorageReference>>  = flow{
        firebaseAuth.currentUser?.let { user->
            emit(ResultState.Loading(null))
            try {
                val ref = storageRef.child(user.uid)
                ref.putFile(uri).await()
                Log.d("glide", ref.toString())
                emit(ResultState.Success(ref))

            }catch (e: Exception){
                emit(ResultState.Error(e.message))
            }

        }
    }

    override fun writeMessage(nameUser: String, message: Message, room: Room): Boolean {
        return try {
            room.roomId?.let { database.child("rooms").child(it).setValue(message) }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getRoomById(roomId: String): ResultState<Room> {
        return try {
            val result = database.child("rooms").child(roomId).get().await().getValue(Room::class.java)
            ResultState.Success(result)
        } catch (e: Exception) {
            ResultState.Error(e.message)
        }
    }

    override fun createRoom(room: Room): Boolean {
        return try {
            room.roomId?.let { database.child("rooms").child(it).setValue(room) }
            true
        } catch (e: Exception) {

            false
        }
    }
}

