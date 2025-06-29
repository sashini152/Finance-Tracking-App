package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)

            // Initialize views
            etCurrentPassword = findViewById(R.id.etCurrentPassword)
            etNewPassword = findViewById(R.id.etNewPassword)
            etConfirmPassword = findViewById(R.id.etConfirmPassword)
            btnChangePassword = findViewById(R.id.btnChangePassword)
            btnBack = findViewById(R.id.btnBack)

            // Set up back button
            btnBack.setOnClickListener {
                finish() // Just finish the activity to go back to previous screen
            }

            // Set up change password button
            btnChangePassword.setOnClickListener {
                try {
                    val user = UserManager.getCurrentUser()
                    if (user != null) {
                        val currentPassword = etCurrentPassword.text.toString()
                        val newPassword = etNewPassword.text.toString()
                        val confirmPassword = etConfirmPassword.text.toString()

                        // Validate inputs
                        if (currentPassword.isEmpty()) {
                            Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (newPassword.isEmpty()) {
                            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (confirmPassword.isEmpty()) {
                            Toast.makeText(this, "Please confirm new password", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (newPassword != confirmPassword) {
                            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (newPassword.length < 6) {
                            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        // Verify current password
                        if (!UserManager.verifyPassword(user.nicPassport, currentPassword)) {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        // Change password
                        val success = UserManager.changePassword(user.nicPassport, newPassword)
                        if (success) {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                            // Clear fields
                            etCurrentPassword.text?.clear()
                            etNewPassword.text?.clear()
                            etConfirmPassword.text?.clear()
                            
                            // Go back to previous screen
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                        // Redirect to login
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error changing password: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 