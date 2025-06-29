package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var etNicPassport: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UserManager with context
        UserManager.initialize(applicationContext)

        // Check if user is already logged in
        if (UserManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        etNicPassport = findViewById(R.id.etLoginNICPassport)
        etPassword = findViewById(R.id.etLoginPassword)

        findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener {
            val nicPassport = etNicPassport.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nicPassport.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (UserManager.loginUser(nicPassport, password)) {
                // Start MainActivity and clear the back stack
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<android.widget.TextView>(R.id.tvRegisterNew).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<android.widget.TextView>(R.id.tvForgotPassword).setOnClickListener {
            // TODO: Implement forgot account number functionality
            Toast.makeText(this, "Please contact support for account recovery", Toast.LENGTH_LONG).show()
        }
    }
} 