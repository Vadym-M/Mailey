package com.devx.mailey.presentation.core.profile.field

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devx.mailey.data.model.User
import com.devx.mailey.databinding.FragmentFieldBinding
import dagger.hilt.android.AndroidEntryPoint
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.util.FirebaseConstants.FULL_NAME

@AndroidEntryPoint
class FieldFragment() : Fragment() {
    lateinit var binding: FragmentFieldBinding
    private val viewModel: FieldViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFieldBinding.inflate(inflater, container, false)
        onSavePressed()

        return binding.root
    }

    private fun onSavePressed() {
        binding.apply {
            binding.profileFieldSaveChanges.setOnClickListener {
                viewModel.changeField(coreViewModel.getFieldName()!!, binding.Field.text.toString())
                coreViewModel.setFieldName(null)
                coreViewModel.backPressed()
            }
        }
    }
}