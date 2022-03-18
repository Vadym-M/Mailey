package com.devx.mailey.data.repository

import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val databaseService: DatabaseService) {

 private val userMutex = Mutex()
 private var currentUser: User? = null
 suspend fun getCurrentUserData(): User {
  if (currentUser == null) {
   val user = databaseService.getCurrentUserData()
   userMutex.withLock {
    this.currentUser = user
   }
  }
  return userMutex.withLock { this.currentUser!! }
 }

 suspend fun addImageUrl(url: String){
  val urls = currentUser?.imagesUrl
  val res = if(urls?.add(url) == true) urls else listOf(url)
  databaseService.updateImagesUrl(res)
 }
 suspend fun searchUserByName(str:String) = databaseService.searchUserByName(str)
 suspend fun createRoom(room: Room) = databaseService.createRoom(room)
 suspend fun pushRoomIdToUser(roomId:String, userId:String) = databaseService.pushRoomIdToUser(roomId, userId)
 suspend fun isRoomExist(room: String) = databaseService.isRoomExist(room)
 suspend fun getRoomById(roomId: String) = databaseService.getRoomById(roomId)
 suspend fun pushMessage(roomId: String, msg: Message) = databaseService.pushMessage(roomId, msg)
 fun addMessageListener(liveData: MutableLiveData<MutableMap<String, Message>>) = databaseService.addMessageListener(liveData)
}