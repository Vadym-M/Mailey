package com.devx.mailey.presentation.core.profile.field

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.model.User
import com.devx.mailey.databinding.FragmentFieldBinding
import dagger.hilt.android.AndroidEntryPoint
import com.devx.mailey.presentation.core.CoreViewModel

@AndroidEntryPoint
class FieldFragment(private var fieldName: String) : Fragment() {
    lateinit var binding: FragmentFieldBinding
    private val viewModel: FieldViewModel by activityViewModels()
    private lateinit var coreViewModel: CoreViewModel

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private val _saveBtnVisible = MutableLiveData<Int>()
    val saveBtnVisible: LiveData<Int>
        get() = _saveBtnVisible

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFieldBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun onSavePressed() {
        binding.apply {
            binding.profileFieldSaveChanges.setOnClickListener {
                viewModel.changeField(fieldName, binding.Field.toString())
                coreViewModel.setFragment(null)
            }
        }
    }
}