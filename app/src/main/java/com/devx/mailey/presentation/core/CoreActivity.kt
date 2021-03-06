package com.devx.mailey.presentation.core

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.devx.mailey.R
import com.devx.mailey.databinding.ActivityCoreBinding
import com.devx.mailey.presentation.core.home.HomeFragment
import com.devx.mailey.presentation.core.profile.ProfileFragment
import com.devx.mailey.presentation.core.search.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoreActivity : AppCompatActivity(){
    private val coreViewModel: CoreViewModel by viewModels()
    lateinit var binding: ActivityCoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreViewModel.fetchCurrentUser()

        coreViewModel.onFragmentChanged.observe(this){ fragment->
            fragment.getContentIfNotHandled()?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.coreFragmentContainer, it)
                        .addToBackStack(null)
                        .commit()
            }
        }
        coreViewModel.backPressed.observe(this){
            it.getContentIfNotHandled()?.let {
                onBackPressed()
            }
        }
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState == null){
            showFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    showFragment(HomeFragment())
                    true
                }
                R.id.search -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    showFragment(SearchFragment())
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }

        }
    }

    private fun showFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.coreFragmentContainer, fragment)
            .commit()
    }

}