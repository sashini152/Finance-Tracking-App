package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val TAG = "ProfileActivity"
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileActivity onCreate called")
        
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user data from intent or UserManager
        val nicPassport = intent.getStringExtra("NIC_PASSPORT")
        val accountNumber = intent.getStringExtra("ACCOUNT_NUMBER")
        val phone = intent.getStringExtra("PHONE")

        // If no data in intent, try to get from UserManager
        if (nicPassport == null || accountNumber == null || phone == null) {
            val user = UserManager.getCurrentUser()
            if (user != null) {
                // Display user data
                binding.tvName.text = "NIC/Passport: ${user.nicPassport}"
                binding.tvEmail.text = "Account Number: ${user.accountNumber}"
                binding.tvPhone.text = "Phone: ${user.phone}"
            } else {
                // If no user data available, show error and go back to login
                Toast.makeText(this, "No user data available", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
        } else {
            // Display user data from intent
            binding.tvName.text = "NIC/Passport: $nicPassport"
            binding.tvEmail.text = "Account Number: $accountNumber"
            binding.tvPhone.text = "Phone: $phone"
        }

        // Set up button click listeners
        binding.btnEditProfile.setOnClickListener {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit profile feature coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            UserManager.logout()
            // Return to login screen
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ProfileActivity onStart called")
    }
} 