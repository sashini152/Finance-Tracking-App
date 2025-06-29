package com.example.personalfinancetracker

import android.content.Context
import android.content.SharedPreferences

class BudgetManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    private val BUDGET_KEY = "budget_"

    companion object {
        private var instance: BudgetManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = BudgetManager(context)
            }
        }

        fun getInstance(): BudgetManager {
            return instance ?: throw IllegalStateException("BudgetManager must be initialized first")
        }

        fun setBudget(accountNumber: String, amount: Double): Boolean {
            return getInstance().setBudget(accountNumber, amount)
        }

        fun getBudget(accountNumber: String): Budget? {
            return getInstance().getBudget(accountNumber)
        }
    }

    private fun setBudget(accountNumber: String, amount: Double): Boolean {
        return try {
            prefs.edit().putFloat(getBudgetKey(accountNumber), amount.toFloat()).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getBudget(accountNumber: String): Budget? {
        val amount = prefs.getFloat(getBudgetKey(accountNumber), 0f)
        return if (amount > 0) Budget(accountNumber, amount.toDouble()) else null
    }

    private fun getBudgetKey(accountNumber: String): String {
        return BUDGET_KEY + accountNumber
    }
}

data class Budget(
    val accountNumber: String,
    val amount: Double
) 