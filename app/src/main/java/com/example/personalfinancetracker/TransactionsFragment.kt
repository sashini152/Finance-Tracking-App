package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val TAG = "TransactionsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        try {
            // Initialize managers
            UserManager.initialize(requireContext())
            TransactionManager.initialize(requireContext())

            // Initialize views
            recyclerView = view.findViewById(R.id.recyclerView)
            val btnAddTransaction = view.findViewById<Button>(R.id.btnAddTransaction)

            // Set up RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            transactionAdapter = TransactionAdapter()
            recyclerView.adapter = transactionAdapter

            // Set up button click listener
            btnAddTransaction.setOnClickListener {
                startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
            }

            // Load transactions
            loadTransactions()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing fragment", e)
            Toast.makeText(context, "An error occurred while initializing the fragment", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadTransactions() {
        try {
            val user = UserManager.getCurrentUser()
            if (user != null) {
                val transactions = TransactionManager.getTransactions(user.accountNumber)
                transactionAdapter.submitList(transactions.sortedByDescending { it.date })
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading transactions", e)
            Toast.makeText(context, "An error occurred while loading transactions", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
} 