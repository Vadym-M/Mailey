package com.devx.mailey.presentation.core.profile

import android.content.Context
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
import com.bumptech.glide.Glide
import com.devx.mailey.databinding.FragmentProfileBinding
import com.devx.mailey.presentation.auth.AuthActivity
import com.devx.mailey.presentation.core.CoreActivity
import com.devx.mailey.util.ResultState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.loadImage(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.getCurrentUserData()
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
        viewModel.onLoadImage.observe(viewLifecycleOwner){ res ->
            when(res){
                is ResultState.Success ->{
                    Glide.with(this)
                        .load(res.result)
                        .circleCrop()
                        .into(binding.profileImage)
                    binding.profileProgressBar.visibility = View.GONE
                }
                is ResultState.Error ->{
                    Toast.makeText(requireContext(), res.msg, Toast.LENGTH_LONG).show()
                    binding.profileProgressBar.visibility = View.GONE
                }
                is ResultState.Loading ->{
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

    private fun userDataObserver(){
        viewModel.userData.observe(viewLifecycleOwner){ user ->
            binding.apply {
                profileEmail.text = user.email
                profileFullName.text = user.fullName
            }
            Glide.with(this)
                .load(user.imagesUrl.last())
                .fitCenter()
                .into(binding.profileImage)
        }
    }


    private fun signOut() {
        viewModel.signOut()
    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

}