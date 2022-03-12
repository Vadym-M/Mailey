package com.devx.mailey.presentation.auth.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentWelcomeBinding
import com.devx.mailey.presentation.auth.sign_in.LoginFragment
import com.devx.mailey.presentation.auth.sign_up.RegisterFragment

class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        buttonsListener()
        return binding.root
    }

    private fun buttonsListener(){
        binding.welcomeSignInBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.welcomeSignUpBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}