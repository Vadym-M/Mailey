package com.devx.mailey.presentation.core.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentProfileBinding
import com.devx.mailey.presentation.auth.AuthActivity
import com.devx.mailey.presentation.core.CoreActivity
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.profile.field.FieldFragment
import com.devx.mailey.util.Constants
import com.devx.mailey.util.FirebaseConstants.ABOUT
import com.devx.mailey.util.FirebaseConstants.FULL_NAME
import com.devx.mailey.util.FirebaseConstants.MOBILE_PHONE
import com.devx.mailey.util.ResultState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.loadImage(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        signOutObserver()
        userDataObserver()
        binding.profileSignOutBtn.setOnClickListener {
            signOut()
        }

        binding.profileLoadImage.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImageObserver()

    }

    private fun loadImageObserver() {
        viewModel.onLoadImage.observe(viewLifecycleOwner) { res ->
            when (res) {
                is ResultState.Success -> {
                    viewModel.addImageToUser(res.result.toString())
                    Glide.with(this)
                        .load(res.result)
                        .centerCrop()
                        .into(binding.profileImage)
                    binding.profileProgressBar.visibility = View.GONE
                }
                is ResultState.Error -> {
                    Toast.makeText(requireContext(), res.msg, Toast.LENGTH_LONG).show()
                    binding.profileProgressBar.visibility = View.GONE
                }
                is ResultState.Loading -> {
                    binding.profileProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun signOutObserver() {
        viewModel.onSignOut.observe(viewLifecycleOwner) { state ->
            if (state) {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                requireContext().startActivity(intent)
                val activity = activity as CoreActivity
                activity.finish()
            } else {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun userDataObserver() {
        coreViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                profileEmail.editText?.setText(user.email)
                profileFullName.editText?.setText(user.fullName)
                profileAbout.editText?.setText(user.about)
                profilePhoneNumber.editText?.setText(user.mobilePhone)
            }
            if (user.imagesUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(user.imagesUrl.last())
                    .centerCrop()
                    .into(binding.profileImage)
            } else {
                Glide.with(this)
                    .load(Constants.IMAGE_BLANK_URL)
                    .centerCrop()
                    .into(binding.profileImage)
            }
            accountBlockListener()
        }
    }


    private fun signOut() {
        viewModel.signOut()

    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

    private fun accountBlockListener() {
        binding.apply {
            profileFullName.editText?.setOnClickListener {
                coreViewModel.setFieldName(FULL_NAME)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.coreFragmentContainer, FieldFragment())
                    .addToBackStack(null)
                    .commit()
            }
            profileAbout.editText?.setOnClickListener {
                coreViewModel.setFieldName(ABOUT)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.coreFragmentContainer, FieldFragment())
                    .addToBackStack(null)
                    .commit()
            }
            profilePhoneNumber.editText?.setOnClickListener {
                coreViewModel.setFieldName(MOBILE_PHONE)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.coreFragmentContainer, FieldFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun otherBlockListener() {
        binding.apply {
            profileAboutApp.setOnClickListener {
                // go to fragment
            }
            profileHelp.setOnClickListener {
                // go to fragment
            }
        }
    }

//    private fun saveBtnObserver() {
//        viewModel.saveBtnVisible.observe(viewLifecycleOwner) {
//            binding.profileSaveChanges.visibility = it
//        }
//    }
}