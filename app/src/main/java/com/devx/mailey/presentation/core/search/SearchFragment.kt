package com.devx.mailey.presentation.core.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devx.mailey.databinding.FragmentSearchBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.UsersAdapter
import com.devx.mailey.presentation.core.chat.ChatFragment
import com.devx.mailey.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()
    lateinit var binding: FragmentSearchBinding
    private val usersAdapter = UsersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewInit()
        adapterClickListener()
        onRoomObserver()
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchUserByName(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun recyclerViewInit() {
        binding.searchRecycler.apply {
            adapter = usersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        viewModel.searchedUsers.observe(viewLifecycleOwner) {
            binding.searchProgressBar.visibility = View.VISIBLE
            when (it) {
                is NetworkResult.Success -> {
                    binding.searchProgressBar.visibility = View.GONE
                    usersAdapter.users = it.result ?: emptyList()

                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                    binding.searchProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun onRoomObserver() {
        viewModel.onLocalRoomIdCreated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                coreViewModel.putRoomData(event.peekContent())
                coreViewModel.setFragment(ChatFragment())
            }
        }
    }


    private fun adapterClickListener() {
        usersAdapter.onItemClick = { user ->
            val currentUser = coreViewModel.getCurrentUser()
            viewModel.createRoomId(user, currentUser.id)
        }

    }

}