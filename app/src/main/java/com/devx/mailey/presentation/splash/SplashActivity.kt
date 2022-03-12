package com.devx.mailey.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.devx.mailey.R
import com.devx.mailey.presentation.auth.AuthActivity
import com.devx.mailey.presentation.auth.AuthViewModel
import com.devx.mailey.presentation.core.CoreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

    }

    override fun onStart() {
        super.onStart()
        viewModel.getUser()
        viewModel.user.observe(this){ user ->
            if(user != null){
                val intent = Intent(this, CoreActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}