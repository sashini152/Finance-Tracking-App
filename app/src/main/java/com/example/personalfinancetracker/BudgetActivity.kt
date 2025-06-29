package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale
import com.google.android.material.textfield.TextInputEditText

class BudgetActivity : AppCompatActivity() {
    private lateinit var etBudgetAmount: TextInputEditText
    private lateinit var btnSetBudget: Button
    private lateinit var btnViewBudget: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)
            BudgetManager.initialize(applicationContext)

            // Initialize views
            etBudgetAmount = findViewById(R.id.etBudgetAmount)
            btnSetBudget = findViewById(R.id.btnSetBudget)
            btnViewBudget = findViewById(R.id.btnViewBudget)
            btnBack = findViewById(R.id.btnBack)

            // Set up back button
            btnBack.setOnClickListener {
                finish()
            }

            // Set up set budget button
            btnSetBudget.setOnClickListener {
                try {
                    val user = UserManager.getCurrentUser()
                    if (user != null) {
                        val amountText = etBudgetAmount.text.toString()
                        if (amountText.isEmpty()) {
                            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val amount = amountText.toDoubleOrNull()
                        if (amount == null) {
                            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val success = BudgetManager.setBudget(user.accountNumber, amount)
                        if (success) {
                            Toast.makeText(this, "Budget set successfully", Toast.LENGTH_SHORT).show()
                            etBudgetAmount.text?.clear()
                        } else {
                            Toast.makeText(this, "Failed to set budget", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error setting budget: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Set up view budget button
            btnViewBudget.setOnClickListener {
                startActivity(Intent(this, BudgetViewActivity::class.java))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadCurrentBudget() {
        val user = UserManager.getCurrentUser()
        if (user != null) {
            val budget = BudgetManager.getBudget(user.accountNumber)
            if (budget != null) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
                formatter.currency = java.util.Currency.getInstance("LKR")
                etBudgetAmount.setText(budget.amount.toString())
            }
        }
    }
}
