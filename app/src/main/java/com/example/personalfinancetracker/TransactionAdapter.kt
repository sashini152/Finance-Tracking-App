package com.example.personalfinancetracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(transaction: Transaction) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
            formatter.currency = java.util.Currency.getInstance("LKR")
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            
            tvDescription.text = transaction.description
            tvAmount.text = formatter.format(transaction.amount)
            tvCategory.text = transaction.category
            tvDate.text = dateFormat.format(transaction.date)
            
            // Set text color based on transaction type
            val color = if (transaction.type == "expense") {
                itemView.context.getColor(android.R.color.holo_red_dark)
            } else {
                itemView.context.getColor(android.R.color.holo_green_dark)
            }
            tvAmount.setTextColor(color)

            // Set click listener for the entire item
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditTransactionActivity::class.java).apply {
                    putExtra("transaction_id", transaction.id)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
