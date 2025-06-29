package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.databinding.ActivityLaunchingBinding

class LaunchingActivity : AppCompatActivity() {
    private val TAG = "LaunchingActivity"
    private lateinit var binding: ActivityLaunchingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LaunchingActivity onCreate called")
        
        binding = ActivityLaunchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar
        supportActionBar?.hide()

        // Delay for 2 seconds before starting LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "Starting LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LaunchingActivity onStart called")
    }
} 