package com.devx.mailey.data.model

data class Room(val messages: List<Message>?, val roomId: String?, val usersId: List<String>?){
    constructor(): this(null, null, null)
}
