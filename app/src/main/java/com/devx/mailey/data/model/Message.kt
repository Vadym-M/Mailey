package com.devx.mailey.data.model

data class Message(val text : String, val userName: String) {
    constructor(): this("", "")
}
