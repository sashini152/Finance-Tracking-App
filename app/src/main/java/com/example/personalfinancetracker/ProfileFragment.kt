package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    private lateinit var tvNicPassport: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAccountNumber: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // Initialize views
        tvNicPassport = view.findViewById(R.id.tvUsername)
        tvPhone = view.findViewById(R.id.tvEmail)
        tvAccountNumber = view.findViewById(R.id.tvAccountNumber)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnLogout = view.findViewById(R.id.btnLogout)
        
        // Load user data
        loadUserData()
        
        // Set up button click listeners
        setupClickListeners()
        
        return view
    }
    
    private fun loadUserData() {
        val user = UserManager.getCurrentUser()
        if (user != null) {
            tvNicPassport.text = "NIC/Passport: ${user.nicPassport}"
            tvPhone.text = "Phone: ${user.phone}"
            tvAccountNumber.text = "Account: ${user.accountNumber}"
        } else {
            // If no user is logged in, redirect to login
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
    
    private fun setupClickListeners() {
        // Edit Profile button
        btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        
        // Change Password button
        btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }
        
        // Logout button
        btnLogout.setOnClickListener {
            UserManager.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
} 