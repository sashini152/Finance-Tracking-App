package com.example.personalfinancetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class SummaryFragment : Fragment() {

    private lateinit var tvTotalSpent: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvBalance: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val TAG = "SummaryFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_summary, container, false)

        try {
            // Initialize managers
            UserManager.initialize(requireContext())
            TransactionManager.initialize(requireContext())

            // Initialize views
            tvTotalSpent = view.findViewById(R.id.tvTotalSpent)
            tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
            tvBalance = view.findViewById(R.id.tvBalance)
            recyclerView = view.findViewById(R.id.recyclerView)

            // Set up RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            transactionAdapter = TransactionAdapter()
            recyclerView.adapter = transactionAdapter

            // Load and display summary
            loadSummary()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing fragment", e)
            Toast.makeText(context, "An error occurred while initializing the fragment", Toast.LENGTH_SHORT).show()
        }

        return view
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
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading summary", e)
            Toast.makeText(context, "An error occurred while loading the summary", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSummary()
    }
} 