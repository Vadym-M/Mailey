package com.devx.mailey.presentation.core.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentHomeBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.RoomAdapter
import com.devx.mailey.presentation.core.adapters.UsersAdapter
import com.devx.mailey.presentation.core.chat.ChatFragment
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
        recyclerViewInit()
        roomChangedObserver()
        onRoomChanged()
        adapterClickListener()
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
                Log.d("debug", "viewModel")
                roomsAdapter.rooms = it.toMutableList()
            }

        }
    }

    private fun roomChangedObserver(){
//        viewModel.roomChanged.observe(viewLifecycleOwner){
//            roomsAdapter.changeLastMessage(it)
//            roomsAdapter.notifyDataSetChanged()
//        }
    }

    private fun onRoomChanged(){
        coreViewModel.roomsChanged.observe(viewLifecycleOwner){
            Log.d("debug", "coreViewModel")
            roomsAdapter.rooms = it
            roomsAdapter.notifyDataSetChanged()
        }
    }

    private fun adapterClickListener(){
        roomsAdapter.onItemClick = {
          coreViewModel.putRoomId(it)
            coreViewModel.setFragment(ChatFragment())
        }
    }
}