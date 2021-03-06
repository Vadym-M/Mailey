package com.devx.mailey.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String? = null,
    val text: String? = null,
    val userId: String? = null,
    val timestamp: Long,
    val imageUrl: String? = null,
    val userName: String? = null
) :
    Parcelable {
    constructor() : this("", "", "", 0, "", "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "if" to id,
            "text" to text,
            "userId" to userId,
            "timestamp" to ServerValue.TIMESTAMP,
            "imageUrl" to imageUrl,
            "userName" to userName
        )
    }
}
