package com.devx.mailey.data.model

import android.os.Parcelable
import com.google.firebase.storage.StorageReference
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(var fullName: String, val email:String, val id:String, val rooms:HashMap<String,String>?, val imagesUrl: MutableList<String>, var about: String = "", var mobilePhone: String = "") :
    Parcelable {
    constructor() : this("", "", "", null, mutableListOf())
}
