package com.devx.mailey.data.firebase.impl

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.firebase.AuthService
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.firebase.StorageService
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.util.FirebaseConstants.ABOUT
import com.devx.mailey.util.FirebaseConstants.EMAIL
import com.devx.mailey.util.FirebaseConstants.FULL_NAME
import com.devx.mailey.util.FirebaseConstants.IMAGES_URL
import com.devx.mailey.util.FirebaseConstants.MESSAGES
import com.devx.mailey.util.FirebaseConstants.MOBILE_PHONE
import com.devx.mailey.util.FirebaseConstants.ROOMS
import com.devx.mailey.util.FirebaseConstants.USERS
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirebaseSource : AuthService, StorageService, DatabaseService {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val database: DatabaseReference by lazy { Firebase.database.reference }
    private val storageRef: StorageReference by lazy { Firebase.storage.reference }

    var currentUserRef: DatabaseReference? = null

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
            currentUserRef = database.child(USERS).child(firebaseAuth.currentUser!!.uid)
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
                currentUserRef = database.child(USERS).child(firebaseAuth.currentUser!!.uid)
                emit(ResultState.Success(result))
            } catch (e: Exception) {
                emit(ResultState.Error(e.message))
            }
        }

    override suspend fun getUser(): FirebaseUser? {
        firebaseAuth.currentUser?.let {
            currentUserRef = database.child(USERS).child(firebaseAuth.currentUser!!.uid)
        }
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


    override suspend fun getCurrentUserData(): User? {
        return CurrentUser.init?.get()?.await()?.getValue(User::class.java)
    }

    override suspend fun updateImagesUrl(urls: List<String>) {
        CurrentUser.imagesUrl?.setValue(urls)?.await()
    }

    override suspend fun getRoomById(roomId: String): Room {
        return FirebaseRoom.ref(roomId).get().await().getValue(Room::class.java)!!

    }

    override suspend fun createRoom(room: Room): Boolean {
        FirebaseRoom.ref(room.roomId).setValue(room)
        return true
    }

    override suspend fun searchUserByName(str: String): Flow<ResultState<List<User>>> = flow {
        emit(ResultState.Loading(null))
        try {
            val list = mutableListOf<User>()
            val response = FirebaseUsers.ref().get().await().children
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
        val key = FirebaseUsers.refUserId(userId).child(ROOMS).push().key
        val values = mapOf("/users/$userId/rooms/$roomId" to key)
        database.updateChildren(values).await()
    }

    override suspend fun isRoomExist(roomId: String): Boolean {
       return FirebaseRoom.ref(roomId).get().await().exists()
    }

    override suspend fun pushMessage(roomId: String, msg: Message) {
        val key = FirebaseRoom.refMessages(roomId).push().key
        val postValues = msg.toMap()
        val value = mapOf("/rooms/$roomId/messages/$key" to postValues)
        database.updateChildren(value)

    }

    override fun addMessageListener(
        liveData: MutableLiveData<MutableMap<String, Message>>,
        roomId: String
    ) {
       FirebaseRoom.refMessages(roomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue<Message>()!!
                    val key = snapshot.key.toString()
                    val test = mutableMapOf(key to msg)
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

    override suspend fun onRoomsChanged(
        liveData: MutableLiveData<String>, userId: String
    ){
        FirebaseUsers.refUserId(userId).child(ROOMS)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.key
                    liveData.postValue(key)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.key
                    liveData.postValue(key)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override suspend fun pushRoomChanged(userId: String, roomId: String) {
        val key = FirebaseUsers.refUserId(userId).child("updatedRooms").push().key
        val value = mapOf("/users/$userId/updatedRooms/$roomId" to key)
        database.updateChildren(value)
    }

    fun getDatabaseRef(): DatabaseReference{
        return database
    }

    override fun changeUserFullName(value: String) {
        CurrentUser.fullName?.setValue(value)
    }

    override fun changeUserAbout(value: String) {
        CurrentUser.about?.setValue(value)
    }

    override fun changeUserMobilePhone(value: String) {
        CurrentUser.mobilePhone?.setValue(value)
    }
}

class CurrentUser {
    companion object {
        val init = FirebaseSource.currentUserRef
        val imagesUrl = init?.child(IMAGES_URL)
        val email = init?.child(EMAIL)
        val fullName = init?.child(FULL_NAME)
        val about = init?.child(ABOUT)
        val rooms = init?.child(ROOMS)
        val mobilePhone = init?.child(MOBILE_PHONE)
    }
}
class FirebaseRoom{
    companion object{
        fun ref(id:String):DatabaseReference{
            return FirebaseSource.getDatabaseRef().child(ROOMS).child(id)
        }
        fun refMessages(id:String):DatabaseReference{
            return FirebaseSource.getDatabaseRef().child(ROOMS).child(id).child(MESSAGES)
        }
    }
}
class FirebaseUsers{
    companion object{
        fun ref():DatabaseReference{
            return FirebaseSource.getDatabaseRef().child(USERS)
        }
        fun refUserId(userId: String):DatabaseReference{
            return FirebaseSource.getDatabaseRef().child(USERS).child(userId)
        }
    }
}




