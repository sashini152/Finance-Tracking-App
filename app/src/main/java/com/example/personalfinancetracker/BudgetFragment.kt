package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        val btnSetBudget = view.findViewById<Button>(R.id.btnSetBudget)
        val btnViewBudget = view.findViewById<Button>(R.id.btnViewBudget)

        btnSetBudget.setOnClickListener {
            startActivity(Intent(requireContext(), BudgetActivity::class.java))
        }

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

        return view
    }
} 