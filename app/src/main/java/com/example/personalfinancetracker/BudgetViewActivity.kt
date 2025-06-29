package com.example.personalfinancetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewActivity : AppCompatActivity() {
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvSpentAmount: TextView
    private lateinit var tvRemainingAmount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var btnEditBudget: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_view)

        try {
            // Initialize managers
            UserManager.initialize(applicationContext)
            TransactionManager.initialize(applicationContext)
            BudgetManager.initialize(applicationContext)

            // Initialize views
            tvTotalBudget = findViewById(R.id.tvTotalBudget)
            tvSpentAmount = findViewById(R.id.tvSpentAmount)
            tvRemainingAmount = findViewById(R.id.tvRemainingAmount)
            progressBar = findViewById(R.id.progressBar)
            pieChart = findViewById(R.id.pieChart)
            barChart = findViewById(R.id.barChart)
            btnEditBudget = findViewById(R.id.btnEditBudget)
            btnBack = findViewById(R.id.btnBack)

            // Set up button listeners
            btnEditBudget.setOnClickListener {
                startActivity(Intent(this, BudgetActivity::class.java))
            }

            btnBack.setOnClickListener {
                finish()
            }

            // Set up charts
            setupPieChart()
            setupBarChart()

            // Load and display budget data
            loadBudgetData()
        } catch (e: IllegalStateException) {
            // Handle case where managers are not initialized properly
            Toast.makeText(this, "Please restart the app", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupPieChart() {
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1000)
        pieChart.legend.isEnabled = true
    }

    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(12)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 12
        
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.spaceTop = 35f
        leftAxis.axisMinimum = 0f
        
        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f
        
        barChart.legend.isEnabled = false
        barChart.animateY(1000)
    }

    private fun loadBudgetData() {
        try {
            val user = UserManager.getCurrentUser()
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val budget = BudgetManager.getBudget(user.accountNumber)
            if (budget != null) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
                formatter.currency = Currency.getInstance("LKR")

                // Calculate spent amount
                val transactions = TransactionManager.getTransactions(user.accountNumber)
                val spentAmount = transactions
                    .filter { it.type == "expense" }
                    .sumOf { it.amount }

                val remainingAmount = budget.amount - spentAmount
                val progress = ((spentAmount / budget.amount) * 100).toInt()

                // Update text views
                tvTotalBudget.text = formatter.format(budget.amount)
                tvSpentAmount.text = formatter.format(spentAmount)
                tvRemainingAmount.text = formatter.format(remainingAmount)
                progressBar.progress = progress

                // Update charts
                updatePieChart(spentAmount, remainingAmount)
                updateBarChart(transactions)
            } else {
                // Show empty state instead of closing
                tvTotalBudget.text = "No budget set"
                tvSpentAmount.text = "0.00"
                tvRemainingAmount.text = "0.00"
                progressBar.progress = 0
                
                // Clear charts
                pieChart.clear()
                barChart.clear()
                
                Toast.makeText(this, "No budget set. Click 'Edit Budget' to set your budget.", Toast.LENGTH_LONG).show()
            }
        } catch (e: IllegalStateException) {
            // Handle case where managers are not initialized properly
            Toast.makeText(this, "Please restart the app", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading budget data: ${e.message}", Toast.LENGTH_SHORT).show()
            // Don't finish the activity on error, just show the error message
        }
    }

    private fun updatePieChart(spentAmount: Double, remainingAmount: Double) {
        try {
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(spentAmount.toFloat(), "Spent"))
            entries.add(PieEntry(remainingAmount.toFloat(), "Remaining"))

            val dataSet = PieDataSet(entries, "Budget Distribution")
            dataSet.colors = listOf(Color.RED, Color.GREEN)
            dataSet.valueTextSize = 12f

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.description.isEnabled = false
            pieChart.animateY(1000)
            pieChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating pie chart: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBarChart(transactions: List<Transaction>) {
        try {
            val monthlyData = Array(12) { 0.0 }
            val calendar = Calendar.getInstance()
            
            // Calculate monthly spending
            transactions
                .filter { it.type == "expense" }
                .forEach { transaction ->
                    calendar.time = transaction.date
                    val month = calendar.get(Calendar.MONTH)
                    monthlyData[month] += transaction.amount
                }

            val entries = ArrayList<BarEntry>()
            val months = ArrayList<String>()
            val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
            
            for (i in 0..11) {
                calendar.set(Calendar.MONTH, i)
                val monthName = dateFormat.format(calendar.time)
                months.add(monthName)
                entries.add(BarEntry(i.toFloat(), monthlyData[i].toFloat()))
            }

            val dataSet = BarDataSet(entries, "Monthly Spending")
            dataSet.color = Color.rgb(33, 150, 243) // Material Blue
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 10f

            val data = BarData(dataSet)
            data.barWidth = 0.8f

            barChart.data = data
            barChart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return months[value.toInt()]
                }
            }
            barChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating bar chart: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBudgetData()
    }
} 