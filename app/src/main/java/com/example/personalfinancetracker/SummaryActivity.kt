package com.example.personalfinancetracker

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class SummaryActivity : AppCompatActivity() {
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvBalance: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val TAG = "SummaryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)
            TransactionManager.initialize(applicationContext)

            // Initialize views
            tvTotalSpent = findViewById(R.id.tvTotalSpent)
            tvTotalIncome = findViewById(R.id.tvTotalIncome)
            tvBalance = findViewById(R.id.tvBalance)
            recyclerView = findViewById(R.id.recyclerView)

            // Set up RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            transactionAdapter = TransactionAdapter()
            recyclerView.adapter = transactionAdapter

            // Load and display summary
            loadSummary()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing activity", e)
            Toast.makeText(this, "An error occurred while initializing the activity", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadSummary() {
        try {
            val user = UserManager.getCurrentUser()
            if (user != null) {
                val transactions = TransactionManager.getTransactions(user.accountNumber)
                val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
                formatter.currency = java.util.Currency.getInstance("LKR")

                // Calculate totals
                val totalSpent = transactions
                    .filter { it.type == "expense" }
                    .sumOf { it.amount }

                val totalIncome = transactions
                    .filter { it.type == "income" }
                    .sumOf { it.amount }

                val balance = totalIncome - totalSpent

                // Update text views
                tvTotalSpent.text = formatter.format(totalSpent)
                tvTotalIncome.text = formatter.format(totalIncome)
                tvBalance.text = formatter.format(balance)

                // Update RecyclerView
                transactionAdapter.submitList(transactions.sortedByDescending { it.date })
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading summary", e)
            Toast.makeText(this, "An error occurred while loading the summary", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSummary()
    }
}
