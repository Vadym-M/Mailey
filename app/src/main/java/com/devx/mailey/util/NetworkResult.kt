package com.devx.mailey.util

sealed class NetworkResult<T>(val result: T?, val msg:String? = null){
    class Success<T>(result: T): NetworkResult<T>(result)
    class Error<T>(msg: String?) : NetworkResult<T>(null, msg)
}

fun <T>NetworkResult<T>.isSuccessful(): Boolean{
   return this is NetworkResult.Success && this.result != null
}