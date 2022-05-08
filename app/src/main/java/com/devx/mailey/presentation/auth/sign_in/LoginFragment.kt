package com.devx.mailey.presentation.auth.sign_in

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentLoginBinding
import com.devx.mailey.util.NetworkResult
import com.devx.mailey.presentation.auth.AuthViewModel
import com.devx.mailey.presentation.auth.AuthStateObserver
import com.devx.mailey.presentation.auth.reset_pass.ResetPassFragment
import com.devx.mailey.presentation.core.CoreActivity
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), AuthStateObserver {

    lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        authStateObserver()
        login()
        emailEditTextStateObserver()
        onBackPressed()
        resetPasswordBtnListener()

        return binding.root
    }

    private fun login() {
        binding.loginSignInBtn.setOnClickListener {
            val email = binding.emailLogin.editText?.text.toString()
            val password = binding.passwordLogin.editText?.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun emailEditTextStateObserver() {
        binding.emailLogin.editText?.setOnFocusChangeListener { view, b ->
            val email = binding.emailLogin.editText?.text.toString()
            binding.emailLogin.error =
                if (!b && !email.isValidEmail()) getString(R.string.email_is_badly_formatted) else null
        }
    }

    private fun resetPasswordBtnListener(){
        binding.forgotPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, ResetPassFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    override fun authStateObserver() {
        viewModel.networkResult.observe(viewLifecycleOwner) {
            binding.loginProgressBar.visibility = View.VISIBLE
            when (it) {
                is NetworkResult.Success -> {
                    binding.loginProgressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), CoreActivity::class.java)
                    requireContext().startActivity(intent)
                    activity?.finish()
                }
                is NetworkResult.Error -> {
                    binding.loginProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun onBackPressed() {
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}