package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var btnAddTransaction: Button
    private lateinit var btnViewBudget: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnProfile: Button
    private lateinit var btnSummary: Button
    private lateinit var btnLogout: Button  // New button init

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize buttons
        btnAddTransaction = view.findViewById(R.id.btnAddTransaction)
        btnViewBudget = view.findViewById(R.id.btnViewBudget)
        btnSetBudget = view.findViewById(R.id.btnSetBudget)
        btnProfile = view.findViewById(R.id.btnProfile)
        btnSummary = view.findViewById(R.id.btnSummary)
        btnLogout = view.findViewById(R.id.btnLogout)  // New button init

        // Add Transaction button
        btnAddTransaction.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }

        // View Budget button
        btnViewBudget.setOnClickListener {
            val user = UserManager.getCurrentUser()
            if (user != null) {
                val budget = BudgetManager.getBudget(user.accountNumber)
                if (budget != null) {
                    startActivity(Intent(requireContext(), BudgetViewActivity::class.java))
                } else {
                    Toast.makeText(requireContext(), "No budget set yet", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Set Budget button
        btnSetBudget.setOnClickListener {
            startActivity(Intent(requireContext(), BudgetActivity::class.java))
        }

        // Summary button
        btnSummary.setOnClickListener {
            startActivity(Intent(requireContext(), SummaryActivity::class.java))
        }

        // Profile button — navigate to ProfileFragment
        btnProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // Logout button
        btnLogout.setOnClickListener {
            // Clear any stored user data to log the user out (if applicable)
            UserManager.logout()

            // Redirect to LoginActivity
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()  // Close the current activity so the user cannot navigate back
        }

        return view
    }

    private fun showCurrentBudgetNotification() {
        val user = UserManager.getCurrentUser()
        if (user != null) {
            val budget = BudgetManager.getBudget(user.accountNumber)
            if (budget != null) {
                NotificationHelper.showCurrentBudgetNotification(
                    requireContext(),
                    budget.amount
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showCurrentBudgetNotification()
    }
}
