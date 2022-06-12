package com.devx.mailey.presentation.core.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devx.mailey.databinding.FragmentChatBinding
import com.devx.mailey.presentation.core.CoreViewModel
import com.devx.mailey.presentation.core.adapters.ChatAdapter
import com.devx.mailey.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val permissionId = 2

    private val viewModel: ChatViewModel by viewModels()
    private val coreViewModel: CoreViewModel by activityViewModels()
    lateinit var binding: FragmentChatBinding
    private val usersAdapter = ChatAdapter()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        onBackPressed()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
        toastMessageListener()
        binding.locationBtn.setOnClickListener {
            getLocation()
        }
        binding.sendBtn.setOnClickListener {

            if (binding.chatEditText.text.toString().trim().isNotEmpty()) {
                viewModel.sendMessage(binding.chatEditText.text.toString())
            }
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

    private fun initUserObserver() {
        viewModel.initUser.observe(viewLifecycleOwner) {
            initUser(it.first, it.second)
        }
    }

    private fun initUser(fullName: String, imageUrl: String?) {
        binding.chatUserName.text = fullName
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(binding.chatUserImage)
        } else {
            Glide.with(this)
                .load(Constants.IMAGE_BLANK_URL)
                .circleCrop()
                .into(binding.chatUserImage)
        }

    }

    private fun onBackPressed() {
        binding.chatBackBtn.setOnClickListener {
            coreViewModel.backPressed()
        }
    }

    private fun progressBarListener() {
        viewModel.progressBar.observe(viewLifecycleOwner) {
            binding.chatProgressBar.visibility = it
        }
    }

    private fun toastMessageListener() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        viewModel.sendMessage(list[0].getAddressLine(0))
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

}