package com.devx.mailey.presentation.auth.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.imageView, "rocket_image")
    }

    private fun buttonsListener(){
        binding.welcomeSignInBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LoginFragment())
                .addSharedElement(binding.imageView, "login_rocket_image")
                .addToBackStack(null)
                .commit()
        }
        binding.welcomeSignUpBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, RegisterFragment())
                .addSharedElement(binding.imageView, "register_rocket_image")
                .addToBackStack(null)
                .commit()
        }
    }

}