package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    private lateinit var etNicPassport: TextInputEditText
    private lateinit var etAccountNumber: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        try {
            // Initialize managers first
            UserManager.initialize(applicationContext)

            // Initialize views
            etNicPassport = findViewById(R.id.etNicPassport)
            etAccountNumber = findViewById(R.id.etAccountNumber)
            etPhone = findViewById(R.id.etPhone)
            btnSave = findViewById(R.id.btnSave)
            btnBack = findViewById(R.id.btnBack)

            // Set up back button
            btnBack.setOnClickListener {
                finish() // This will return to the previous activity (Profile)
            }

            // Load current user data
            loadUserData()

            // Set up save button
            btnSave.setOnClickListener {
                try {
                    val currentUser = UserManager.getCurrentUser()
                    if (currentUser != null) {
                        val nicPassport = etNicPassport.text.toString()
                        val accountNumber = etAccountNumber.text.toString()
                        val phone = etPhone.text.toString()

                        // Validate inputs
                        if (nicPassport.isEmpty()) {
                            Toast.makeText(this, "Please enter NIC/Passport", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (accountNumber.isEmpty()) {
                            Toast.makeText(this, "Please enter account number", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (phone.isEmpty()) {
                            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        // Update user data
                        val success = UserManager.updateUser(
                            currentUser.nicPassport,
                            nicPassport,
                            accountNumber,
                            phone
                        )

                        if (success) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            // Return to profile page
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                        // Navigate to login
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating profile", e)
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing activity", e)
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadUserData() {
        try {
            val currentUser = UserManager.getCurrentUser()
            if (currentUser != null) {
                etNicPassport.setText(currentUser.nicPassport)
                etAccountNumber.setText(currentUser.accountNumber)
                etPhone.setText(currentUser.phone)
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                // Navigate to login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user data", e)
            Toast.makeText(this, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 