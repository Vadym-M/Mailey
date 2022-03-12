package com.devx.mailey.presentation.auth.sign_up

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentRegisterBinding
import com.devx.mailey.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        register()
        toastListener()
        progressBar()
        return binding.root
    }

    //private fun
    private fun progressBar(){
        viewModel.progressBar.observe(viewLifecycleOwner){
            binding.registerProgressBar.visibility = it
        }
    }

    private fun toastListener() {
        viewModel.toastMsg.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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