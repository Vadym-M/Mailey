package com.devx.mailey.data.firebase.impl

import android.net.Uri
import androidx.lifecycle.MutableLiveData
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
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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


    override suspend fun getRooms() {}
    override fun writeMessage(user: User, message: Message, room: Room) {}
    override fun writeMessage(nameUser: String, message: Message, room: Room): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUserData(): User? {
        return CurrentUser.init.get().await().getValue(User::class.java)
    }

    override suspend fun updateImagesUrl(urls: List<String>) {
        CurrentUser.imagesUrl.setValue(urls).await()
    }

    override suspend fun getRoomById(roomId: String): Room {
        return database.child("rooms").child(roomId).get().await().getValue(Room::class.java)!!
    }

    override suspend fun createRoom(room: Room): Boolean {
        database.child("rooms").child(room.roomId).setValue(room)
        return true
    }

    override suspend fun searchUserByName(str: String): Flow<ResultState<List<User>>> = flow {
        emit(ResultState.Loading(null))
        try {
            val list = mutableListOf<User>()
            val response = database.child("users").get().await().children
            for (i in response) {
                val user = i.getValue(User::class.java)!!
                if (user.fullName.lowercase().contains(str)) {
                    list.add(user)
                }
            }
            emit(ResultState.Success(list))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    override suspend fun pushRoomIdToUser(roomId: String, userId: String) {
        val key = database.child("users").child(userId).child("rooms").push().key
        val values = mapOf("/users/$userId/rooms/$key" to roomId)
        database.updateChildren(values)
    }

    override suspend fun isRoomExist(roomId: String): Boolean {
       return database.child("rooms").child(roomId).get().await().exists()
    }

    override suspend fun pushMessage(roomId: String, msg: Message) {
        val key = database.child("rooms").child(roomId).child("messages").push().key
        val value = mapOf("/rooms/$roomId/messages/$key" to msg)
        database.updateChildren(value)
    }

    override fun addMessageListener(liveData: MutableLiveData<MutableMap<String, Message>>) {
       database.child("rooms").child("YLbPiWXbUuZXihZxPFOt6iPSUVC6").child("messages").addChildEventListener( object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               val msg = snapshot.getValue<Message>()!!
                val key = snapshot.key.toString()!!
                val test = mutableMapOf<String, Message>(key to msg)
                liveData.postValue(test)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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




