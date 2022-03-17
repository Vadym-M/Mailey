package com.devx.mailey.data.firebase.impl

import android.net.Uri
import com.devx.mailey.data.firebase.AuthService
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.firebase.StorageService
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

    lateinit var currentUserRef: DatabaseReference

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Flow<ResultState<AuthResult>> = flow {

        emit(ResultState.Loading(null))
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid.let { id ->
                val user = User(
                    fullName = fullName,
                    email = email,
                    id = id.toString(),
                    null,
                    mutableListOf()
                )
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

    override suspend fun getUser(): FirebaseUser? {
        initUser(firebaseAuth.currentUser!!.uid)
        return firebaseAuth.currentUser
    }

    override fun signOut(): Boolean {
        return try {
            firebaseAuth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loadImage(uri: Uri): Flow<ResultState<String>> = flow {
        firebaseAuth.currentUser?.let { user ->
            emit(ResultState.Loading(null))
            try {
                val ref = storageRef.child(user.uid).child(uri.lastPathSegment.toString())
                ref.putFile(uri).await()
                val url = ref.downloadUrl.await().toString()
                emit(ResultState.Success(url))

            } catch (e: Exception) {
                emit(ResultState.Error(e.message))
            }

        }
    }

    override suspend fun deleteImage(ref: StorageReference) {
        ref.delete().await()
    }

    fun writeMessage(nameUser: String, message: Message, room: Room) {
        val key = database.child("messages").push().key ?: return
    }

    override suspend fun getRooms() {}
    override fun writeMessage(user: User, message: Message, room: Room) {}
    override suspend fun getCurrentUserData(): User? {
        return CurrentUser.init.get().await().getValue(User::class.java)
    }

    override suspend fun updateImagesUrl(urls: List<String>) {
        CurrentUser.imagesUrl.setValue(urls).await()
        //database.child("users").child(currentUser!!.id).child("imagesUrl").setValue(urls).await()
    }

    private fun initUser(id: String) {
        currentUserRef = database.child("users").child(id)
    }

}

class CurrentUser {
    companion object {
        val init = FirebaseSource.currentUserRef
        val imagesUrl = init.child("imagesUrl")
    }
}




