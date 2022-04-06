package com.devx.mailey.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Room(var messages: MutableMap<String, Message>, val roomId: String, val firstUserId: String, val firstUserName:String, val secondUserId: String, val secondUserName: String) :
    Parcelable {
    constructor(): this(HashMap(), "",  "", "", "","")

}
