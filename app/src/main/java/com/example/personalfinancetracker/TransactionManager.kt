package com.example.personalfinancetracker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TransactionManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("transaction_prefs", Context.MODE_PRIVATE)
    private val TRANSACTIONS_KEY = "transactions"
    private val gson = Gson()
    private val TAG = "TransactionManager"

    companion object {
        private var instance: TransactionManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = TransactionManager(context)
            }
        }

        fun getInstance(): TransactionManager {
            return instance ?: throw IllegalStateException("TransactionManager must be initialized first")
        }

        fun addTransaction(accountNumber: String, amount: Double, description: String, category: String, type: String): Boolean {
            return getInstance().addTransaction(accountNumber, amount, description, category, type)
        }

        fun getTransactions(accountNumber: String): List<Transaction> {
            return getInstance().getTransactions(accountNumber)
        }

        fun updateTransaction(id: Long, amount: Double, description: String, category: String, type: String): Boolean {
            return getInstance().updateTransaction(id, amount, description, category, type)
        }

        fun deleteTransaction(id: Long): Boolean {
            return getInstance().deleteTransaction(id)
        }

        fun getTotalSpentAmount(accountNumber: String): Double {
            return getInstance().getTotalSpentAmount(accountNumber)
        }

        fun getMonthlySpending(accountNumber: String): List<Double> {
            return getInstance().getMonthlySpending(accountNumber)
        }
    }

    private fun addTransaction(accountNumber: String, amount: Double, description: String, category: String, type: String): Boolean {
        return try {
            Log.d(TAG, "Adding transaction: $description, $amount, $category, $type")
            val transactions = getTransactions(accountNumber).toMutableList()
            val newTransaction = Transaction(
                id = System.currentTimeMillis(),
                accountNumber = accountNumber,
                amount = amount,
                description = description,
                category = category,
                type = type,
                date = Date()
            )
            transactions.add(newTransaction)
            saveTransactions(transactions)
            Log.d(TAG, "Transaction added successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding transaction", e)
            false
        }
    }

    private fun updateTransaction(id: Long, amount: Double, description: String, category: String, type: String): Boolean {
        return try {
            Log.d(TAG, "Updating transaction: $id")
            val transactions = loadTransactions().toMutableList()
            val index = transactions.indexOfFirst { it.id == id }
            
            if (index != -1) {
                val updatedTransaction = transactions[index].copy(
                    amount = amount,
                    description = description,
                    category = category,
                    type = type
                )
                transactions[index] = updatedTransaction
                saveTransactions(transactions)
                Log.d(TAG, "Transaction updated successfully")
                true
            } else {
                Log.e(TAG, "Transaction not found: $id")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating transaction", e)
            false
        }
    }

    private fun deleteTransaction(id: Long): Boolean {
        return try {
            Log.d(TAG, "Deleting transaction: $id")
            val transactions = loadTransactions().toMutableList()
            val index = transactions.indexOfFirst { it.id == id }
            
            if (index != -1) {
                transactions.removeAt(index)
                saveTransactions(transactions)
                Log.d(TAG, "Transaction deleted successfully")
                true
            } else {
                Log.e(TAG, "Transaction not found: $id")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting transaction", e)
            false
        }
    }

    fun getTransactions(accountNumber: String): List<Transaction> {
        return try {
            loadTransactions().filter { it.accountNumber == accountNumber }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting transactions", e)
            emptyList()
        }
    }

    private fun getTotalSpentAmount(accountNumber: String): Double {
        return try {
            getTransactions(accountNumber)
                .filter { it.type == "expense" }
                .sumOf { it.amount }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating total spent amount", e)
            0.0
        }
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        try {
            val json = gson.toJson(transactions)
            prefs.edit().putString(TRANSACTIONS_KEY, json).apply()
            Log.d(TAG, "Transactions saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving transactions", e)
            throw e
        }
    }

    private fun loadTransactions(): MutableList<Transaction> {
        return try {
            val json = prefs.getString(TRANSACTIONS_KEY, "[]")
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading transactions", e)
            mutableListOf()
        }
    }

    private fun getMonthlySpending(accountNumber: String): List<Double> {
        return try {
            val transactions = getTransactions(accountNumber)
            val monthlySpending = MutableList(12) { 0.0 }
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            transactions.forEach { transaction ->
                calendar.time = transaction.date
                if (calendar.get(Calendar.YEAR) == currentYear) {
                    val month = calendar.get(Calendar.MONTH)
                    if (transaction.type == "expense") {
                        monthlySpending[month] += transaction.amount
                    }
                }
            }

            monthlySpending
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating monthly spending", e)
            List(12) { 0.0 }
        }
    }
}

data class Transaction(
    val id: Long,
    val accountNumber: String,
    val amount: Double,
    val description: String,
    val category: String,
    val type: String,
    val date: Date
) 