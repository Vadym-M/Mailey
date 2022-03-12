package com.devx.mailey.presentation.core.profile

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentProfileBinding
import com.devx.mailey.presentation.auth.AuthActivity
import com.devx.mailey.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileViewModel.loadImage(uri!!)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        signOutObserver()
        binding.profileSignOutBtn.setOnClickListener {
            signOut()
        }

        binding.profileLoadImage.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    private fun signOutObserver() {
        profileViewModel.onSignOut.observe(viewLifecycleOwner) { state ->
            if (state) {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                requireContext().startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signOut() {
        profileViewModel.signOut()
    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

}