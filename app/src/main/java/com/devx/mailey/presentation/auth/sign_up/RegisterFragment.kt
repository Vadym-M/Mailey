package com.devx.mailey.presentation.auth.sign_up

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import com.devx.mailey.databinding.FragmentRegisterBinding
import com.devx.mailey.util.NetworkResult
import com.devx.mailey.presentation.auth.AuthViewModel
import com.devx.mailey.presentation.auth.AuthStateObserver
import com.devx.mailey.presentation.core.CoreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), AuthStateObserver{

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        register()
        authStateObserver()
        onBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.registerRocketImage, "register_rocket_image")
    }

    override fun authStateObserver() {
        viewModel.networkResult.observe(viewLifecycleOwner) {
            binding.registerProgressBar.visibility = View.VISIBLE
            when (it) {
                is NetworkResult.Success -> {
                    binding.registerProgressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), CoreActivity::class.java)
                    requireContext().startActivity(intent)
                    activity?.finish()
                }
                is NetworkResult.Error -> {
                    binding.registerProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()

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

    private fun onBackPressed() {
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}