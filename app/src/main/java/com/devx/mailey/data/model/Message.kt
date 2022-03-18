package com.devx.mailey.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(val id:String, val text : String, val userId:String, val timestamp: String, val imageUrl:String?, val userName:String) :
    Parcelable {
    constructor(): this("","", "", "","","")
}
