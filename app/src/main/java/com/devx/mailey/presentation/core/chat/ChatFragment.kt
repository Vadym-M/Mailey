package com.devx.mailey.presentation.core.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.databinding.FragmentChatBinding
import com.devx.mailey.presentation.core.adapters.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private val viewModel: ChatViewModel by activityViewModels()
    lateinit var binding: FragmentChatBinding
    private val usersAdapter = ChatAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val roomId = arguments?.getString("roomId")!!
        val userId = arguments?.getString("userId")!!
        viewModel.initRoom(roomId, userId)
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

    private fun messageListener(){
        viewModel.onMessageAdded.observe(viewLifecycleOwner){
            usersAdapter.messeges = it
            binding.chatRecycler.smoothScrollToPosition(0)
        }
    }

    companion object{
        @JvmStatic
        fun newInstance(roomId: String, userId: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString("roomId", roomId)
                putString("userId", userId)
            }
        }
    }
}