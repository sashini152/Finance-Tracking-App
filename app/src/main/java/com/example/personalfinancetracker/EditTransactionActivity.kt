package com.example.personalfinancetracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditTransactionActivity : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: ImageButton
    private var transactionId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)
            TransactionManager.initialize(applicationContext)
            BudgetManager.initialize(applicationContext)

            // Get transaction ID from intent
            transactionId = intent.getLongExtra("transaction_id", -1)
            if (transactionId == -1L) {
                Toast.makeText(this, "Invalid transaction", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            // Initialize views
            etAmount = findViewById(R.id.etAmount)
            etDescription = findViewById(R.id.etDescription)
            spinnerCategory = findViewById(R.id.spinnerCategory)
            spinnerType = findViewById(R.id.spinnerType)
            btnSave = findViewById(R.id.btnSave)
            btnDelete = findViewById(R.id.btnDelete)
            btnBack = findViewById(R.id.btnBack)

            // Set up spinners
            setupSpinners()

            // Load transaction data
            loadTransactionData()

            // Set up button listeners
            btnSave.setOnClickListener {
                saveTransaction()
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmation()
            }

            btnBack.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
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

    private fun loadTransactionData() {
        val user = UserManager.getCurrentUser()
        if (user != null) {
            val transactions = TransactionManager.getTransactions(user.accountNumber)
            val transaction = transactions.find { it.id == transactionId }
            
            if (transaction != null) {
                etAmount.setText(transaction.amount.toString())
                etDescription.setText(transaction.description)
                
                // Set category spinner selection
                val categoryAdapter = spinnerCategory.adapter as ArrayAdapter<String>
                val categoryPosition = categoryAdapter.getPosition(transaction.category)
                spinnerCategory.setSelection(categoryPosition)

                // Set type spinner selection
                val typeAdapter = spinnerType.adapter as ArrayAdapter<String>
                val typePosition = typeAdapter.getPosition(transaction.type)
                spinnerType.setSelection(typePosition)
            } else {
                Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveTransaction() {
        try {
            val user = UserManager.getCurrentUser()
            if (user != null) {
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

                // Update transaction
                val success = TransactionManager.updateTransaction(
                    id = transactionId,
                    amount = amount,
                    description = description,
                    category = category,
                    type = type
                )

                if (success) {
                    Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTransaction() {
        try {
            val user = UserManager.getCurrentUser()
            if (user != null) {
                val success = TransactionManager.deleteTransaction(transactionId)
                if (success) {
                    Toast.makeText(this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error deleting transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { _, _ ->
                deleteTransaction()
            }
            .setNegativeButton("No", null)
            .show()
    }
} 