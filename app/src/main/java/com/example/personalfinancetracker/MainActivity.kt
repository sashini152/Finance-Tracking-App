package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var isInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Initialize managers first
            UserManager.initialize(applicationContext)
            TransactionManager.initialize(applicationContext)
            BudgetManager.initialize(applicationContext)

            // Check if user is logged in
            val currentUser = UserManager.getCurrentUser()
            if (currentUser == null) {
                // Redirect to login if no user is logged in
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            // Initialize views
            bottomNav = findViewById(R.id.bottom_navigation)
            
            // Set up navigation listener
            bottomNav.setOnItemSelectedListener { item ->
                try {
                    when (item.itemId) {
                        R.id.navigation_home -> {
                            loadFragment(HomeFragment())
                            true
                        }
                        R.id.navigation_budget -> {
                            loadFragment(BudgetFragment())
                            true
                        }
                        R.id.navigation_transactions -> {
                            loadFragment(TransactionsFragment())
                            true
                        }
                        R.id.navigation_summary -> {
                            loadFragment(SummaryFragment())
                            true
                        }
                        R.id.navigation_profile -> {
                            loadFragment(ProfileFragment())
                            true
                        }
                        else -> false
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error loading fragment: ${e.message}", Toast.LENGTH_SHORT).show()
                    false
                }
            }

            // Set default fragment or handle selected tab from intent
            if (savedInstanceState == null) {
                val selectedTab = intent.getIntExtra("selected_tab", R.id.navigation_home)
                bottomNav.selectedItemId = selectedTab
            }

            isInitialized = true
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        if (!isInitialized) return
        
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading fragment: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isInitialized) return
        
        // Check if user is still logged in
        val currentUser = UserManager.getCurrentUser()
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
