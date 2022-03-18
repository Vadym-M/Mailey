package com.devx.mailey.presentation.core.chat

sealed class ChatItems<T>(val data:T?){
    class UserLeft<T>(data: T?): ChatItems<T>(data)
    class UserRight<T>(data: T?): ChatItems<T>(data)
    class Other<T>(data: T?): ChatItems<T>(data)
}
