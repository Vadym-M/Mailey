package com.devx.mailey.presentation.auth.sign_in

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.devx.mailey.databinding.FragmentLoginBinding
import com.devx.mailey.presentation.auth.AuthState
import com.devx.mailey.presentation.auth.AuthViewModel
import com.devx.mailey.presentation.auth.AuthStateObserver
import com.devx.mailey.presentation.core.CoreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), AuthStateObserver {

    lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        authStateObserver()
        login()
        return binding.root
    }

    private fun login() {
        binding.loginSignInBtn.setOnClickListener {
            val email = binding.emailLogin.editText?.text.toString()
            val password = binding.passwordLogin.editText?.text.toString()
            viewModel.login(email, password)
        }
    }

    override fun authStateObserver() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Success -> {
                    binding.loginProgressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), CoreActivity::class.java)
                    requireContext().startActivity(intent)
                    activity?.finish()
                }
                is AuthState.Error -> {
                    binding.loginProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()

                }
                is AuthState.Loading -> {
                    binding.loginProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }


}