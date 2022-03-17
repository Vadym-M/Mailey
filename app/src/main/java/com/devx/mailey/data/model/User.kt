package com.devx.mailey.data.model

import com.google.firebase.storage.StorageReference

data class User(val fullName: String, val email:String, val id:String, val rooms:List<String>?, val imagesUrl: MutableList<String>, val about: String = "", val mobilePhone: String = ""){
    constructor() : this("", "", "", null, mutableListOf())
}
