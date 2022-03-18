package com.devx.mailey.util

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