package com.devx.mailey.presentation.core.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devx.mailey.databinding.FragmentChatBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.ChatAdapter
import com.devx.mailey.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private val viewModel: ChatViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()
    lateinit var binding: FragmentChatBinding
    private val usersAdapter = ChatAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        onBackPressed()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initCurrentUser(coreViewModel.getCurrentUser())
        val room = coreViewModel.getRoomData()
            viewModel.initRoom(room!!)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserObserver()
        progressBarListener()
        binding.sendBtn.setOnClickListener {

            viewModel.sendMessage(binding.chatEditText.text.toString())
            binding.chatEditText.text.clear()

        }
        binding.chatRecycler.apply {
            adapter = usersAdapter
            smoothScrollToPosition(0)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            setHasFixedSize(true)
        }
        messageListener()
    }

    private fun messageListener() {
        viewModel.onMessageAdded.observe(viewLifecycleOwner) {
            usersAdapter.messeges = it
            binding.chatRecycler.smoothScrollToPosition(0)
        }
    }
    private fun initUserObserver(){
        viewModel.initUser.observe(viewLifecycleOwner){
            initUser(it.first, it.second)
        }
    }
    private fun initUser(fullName:String, imageUrl: String?){
        binding.chatUserName.text = fullName
        if (imageUrl != null){
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(binding.chatUserImage)
        }else{
            Glide.with(this)
                .load(Constants.IMAGE_BLANK_URL)
                .circleCrop()
                .into(binding.chatUserImage)
        }

    }
    private fun onBackPressed(){
        binding.chatBackBtn.setOnClickListener {
         coreViewModel.backPressed()
        }
    }
    private fun progressBarListener(){
        viewModel.progressBar.observe(viewLifecycleOwner){
            binding.chatProgressBar.visibility = it
        }
    }
}