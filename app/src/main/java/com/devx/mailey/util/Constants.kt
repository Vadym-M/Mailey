package com.devx.mailey.util

import android.annotation.SuppressLint
import com.devx.mailey.data.model.Message
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object{
        const val IMAGE_BLANK_URL = "https://firebasestorage.googleapis.com/v0/b/smart-messenger-d3069.appspot.com/o/default%2Fcat.jpg?alt=media&token=0dc5eded-d682-4cd5-8b7e-a2f082f7e099"
        const val LEFT_USER = 0
        const val RIGHT_USER = 1
    }
}
fun <T> reverseList(list: List<T>): MutableList<T> {
    return list.indices.map { i: Int -> list[list.size - 1 - i] } as MutableList<T>
}
@SuppressLint("SimpleDateFormat")
fun Long.toDate():String{
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm")
    return format.format(date)
}

fun MutableMap<String, Message>.getLastMessage():String{
    val list = this.values.toMutableList()
    list.sortWith(compareBy { it.timestamp })
    return if(list.isEmpty()) "Empty list" else list.last().text
}
fun MutableMap<String, Message>.getLastMessageTimestamp():String{
    val list = this.values.toMutableList()
    list.sortWith(compareBy { it.timestamp })
    return if(list.isEmpty()) "00:00" else list.last().timestamp.toDate()
}