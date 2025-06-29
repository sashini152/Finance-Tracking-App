package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    private lateinit var etNicPassport: TextInputEditText
    private lateinit var etAccountNumber: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etPhone: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UserManager
        UserManager.initialize(applicationContext)

        etNicPassport = findViewById(R.id.etNICPassport)
        etAccountNumber = findViewById(R.id.etAccountNumber)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etPhone = findViewById(R.id.etPhone)

        findViewById<android.widget.Button>(R.id.btnRegister).setOnClickListener {
            val nicPassport = etNicPassport.text.toString().trim()
            val accountNumber = etAccountNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (nicPassport.isEmpty() || accountNumber.isEmpty() || password.isEmpty() || 
                confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (UserManager.registerUser(accountNumber, nicPassport, password, phone)) {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                // Navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registration failed. User may already exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 