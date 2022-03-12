package com.devx.mailey.presentation.auth.sign_up

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.devx.mailey.databinding.FragmentRegisterBinding
import com.devx.mailey.presentation.auth.AuthState
import com.devx.mailey.presentation.auth.AuthViewModel
import com.devx.mailey.presentation.auth.AuthStateObserver
import com.devx.mailey.presentation.core.CoreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), AuthStateObserver{

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        register()
        authStateObserver()
        return binding.root
    }

    override fun authStateObserver() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Success -> {
                    binding.registerProgressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), CoreActivity::class.java)
                    requireContext().startActivity(intent)
                    activity?.finish()
                }
                is AuthState.Error -> {
                    binding.registerProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()

                }
                is AuthState.Loading -> {
                    binding.registerProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun register(){
        binding.btnRegister.setOnClickListener {
            val fullName = binding.fullNameRegister.editText?.text.toString()
            val email = binding.emailRegister.editText?.text.toString()
            val password = binding.passwordRegister.editText?.text.toString()

            viewModel.register(email = email, password = password, fullName = fullName)
        }
    }


}