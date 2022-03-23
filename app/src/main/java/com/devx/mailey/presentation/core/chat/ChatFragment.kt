package com.devx.mailey.presentation.core.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devx.mailey.databinding.FragmentChatBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.ChatAdapter
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
        return binding.root


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = coreViewModel.getStringPair()!!
        viewModel.initRoom(data.first, data.second)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendBtn.setOnClickListener {

            viewModel.sendMessage(binding.chatEditText.text.toString())

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

}