package com.devx.mailey.presentation.core.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devx.mailey.databinding.FragmentHomeBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.RoomAdapter
import com.devx.mailey.presentation.core.chat.ChatFragment
import com.devx.mailey.util.getUserImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()
    private val roomsAdapter = RoomAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCurrentUserImage()
        recyclerViewInit()
        onRoomChanged()
        adapterClickListener()
        progressBarObserver()
        coreViewModel.user.observe(viewLifecycleOwner){
         viewModel.getUserRooms(it)
        }
    }

    private fun recyclerViewInit() {
        binding.homeRecycler.apply {
            adapter = roomsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)

            viewModel.rooms.observe(viewLifecycleOwner){
                roomsAdapter.rooms = it.toMutableList()
                roomsAdapter.notifyDataSetChanged()
            }

        }
    }


    private fun onRoomChanged(){
        coreViewModel.roomsChanged.observe(viewLifecycleOwner){
            roomsAdapter.rooms = it
            roomsAdapter.notifyDataSetChanged()
        }
    }

    private fun adapterClickListener(){
        roomsAdapter.onItemClick = {
          coreViewModel.putRoomData(it)
            coreViewModel.setFragment(ChatFragment())
        }
    }
    private fun progressBarObserver(){
        viewModel.progressBar.observe(viewLifecycleOwner){
         binding.homeProgressBar.visibility = it
        }
    }

    private fun showCurrentUserImage(){
        coreViewModel.user.observe(viewLifecycleOwner){
            val userImage = it.imagesUrl.getUserImage()
            Glide.with(this)
                .load(userImage)
                .circleCrop()
                .into(binding.homeUserImage)
        }
    }
}