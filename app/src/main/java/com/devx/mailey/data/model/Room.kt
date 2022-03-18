package com.devx.mailey.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Room(val messages: MutableMap<String, Message>, val roomId: String, val leftUserId: String, val rightUserId: String) :
    Parcelable {
    constructor(): this(HashMap(), "",  "", "")

}
