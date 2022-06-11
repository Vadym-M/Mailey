package com.devx.mailey.presentation.auth

import com.devx.mailey.R
import com.devx.mailey.util.isValidEmail

interface AuthStateObserver {
    fun authStateObserver()
    fun emailEditTextStateObserver()
}