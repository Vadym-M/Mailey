package com.devx.mailey.presentation.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

sealed class AuthState<T>(val result: T?, val msg:String? = null){
    class Loading<T>(res: T?): AuthState<T>(res)
    class Success<T>(result: T?): AuthState<T>(result)
    class Error<T>(msg: String?) : AuthState<T>(null, msg)
}
