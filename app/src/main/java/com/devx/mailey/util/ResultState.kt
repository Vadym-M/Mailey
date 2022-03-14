package com.devx.mailey.util

sealed class ResultState<T>(val result: T?, val msg:String? = null){
    class Loading<T>(res: T?): ResultState<T>(res)
    class Success<T>(result: T?): ResultState<T>(result)
    class Error<T>(msg: String?) : ResultState<T>(null, msg)
}
