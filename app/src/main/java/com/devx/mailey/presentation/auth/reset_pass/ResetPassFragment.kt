package com.devx.mailey.presentation.auth.reset_pass

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devx.mailey.R
import com.devx.mailey.databinding.FragmentResetPassBinding

class ResetPassFragment : Fragment() {
    lateinit var binding: FragmentResetPassBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPassBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}