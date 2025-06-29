package com.example.personalfinancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    // Register for notification permission result
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission is required to show transaction alerts", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)
            TransactionManager.initialize(applicationContext)
            BudgetManager.initialize(applicationContext)

            // Request notification permission if needed
            requestNotificationPermission()

            // Initialize notification channel
            NotificationHelper.createNotificationChannel(applicationContext)

            // Initialize views
            etAmount = findViewById(R.id.etAmount)
            etDescription = findViewById(R.id.etDescription)
            spinnerCategory = findViewById(R.id.spinnerCategory)
            spinnerType = findViewById(R.id.spinnerType)
            btnSave = findViewById(R.id.btnSave)
            btnBack = findViewById(R.id.btnBack)

            // Set up spinners
            setupSpinners()

            // Set up button listeners
            btnSave.setOnClickListener {
                saveTransaction()
            }

            btnBack.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show explanation why we need notification permission
                    Toast.makeText(
                        this,
                        "Notification permission is needed to show transaction alerts",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setupSpinners() {
        // Set up category spinner
        val categories = arrayOf("Food", "Transport", "Bills", "Entertainment", "Shopping", "Other")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Set up type spinner
        val types = arrayOf("expense", "income")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter
    }

    private fun saveTransaction() {
        try {
            // Validate input
            val amountStr = etAmount.text.toString()
            val description = etDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val type = spinnerType.selectedItem.toString()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                return
            }

            val amount = amountStr.toDouble()
            val user = UserManager.getCurrentUser()

            if (user != null) {
                // Create and save transaction
                val success = TransactionManager.addTransaction(
                    accountNumber = user.accountNumber,
                    amount = amount,
                    description = description,
                    category = category,
                    type = type
                )

                if (success) {
                    // Check if we have notification permission before showing notification
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED) {
                            NotificationHelper.showTransactionNotification(this, type, amount)
                        }
                    } else {
                        // For older Android versions, just show the notification
                        NotificationHelper.showTransactionNotification(this, type, amount)
                    }
                    
                    Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
