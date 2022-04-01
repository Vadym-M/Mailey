package com.devx.mailey.domain.data

sealed class ChatItems<T>(val data:T?){
    class UserLeft<T>(data: T?): ChatItems<T>(data)
    class UserRight<T>(data: T?): ChatItems<T>(data)
    class Other<T>(data: T?): ChatItems<T>(data)
}
