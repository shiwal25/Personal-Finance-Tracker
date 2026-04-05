package com.example.personalfinancetracker.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionCategory
import com.example.personalfinancetracker.data.TransactionType
import com.example.personalfinancetracker.data.TransactionUiState
import com.example.personalfinancetracker.data.TransactionsRepository
import com.himanshoe.charty.bar.data.BarData
import com.himanshoe.charty.bar.data.BarGroup
import com.himanshoe.charty.color.ChartyColor
import com.himanshoe.charty.line.data.LineData
import com.himanshoe.charty.pie.data.PieData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionViewModel(private val transactionRepository: TransactionsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow("ALL")
    val typeFilter = _typeFilter.asStateFlow()

    val transactionListUiState: StateFlow<List<Transaction>> = combine (
            transactionRepository.getAllTransactionsStream(),
            _searchQuery,
            _typeFilter
        ){  transactions, query, type ->
        transactions.filter { transaction ->

            val matchesType = type == "ALL" || transaction.type == type

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.lowercase(Locale.getDefault())

                val dateString = SimpleDateFormat("MMMM d, yy", Locale.getDefault())
                    .format(Date(transaction.dateTime)).lowercase()

                transaction.category.lowercase().contains(q) ||
                        (transaction.description?.lowercase()?.contains(q) == true) ||
                        transaction.amount.toString().contains(q) ||
                        dateString.contains(q)
            }
            matchesType && matchesQuery
        }
        }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun updateState(newState: TransactionUiState) {
        _uiState.update { newState }
    }

    fun onNumberClick(char: String) {
        val currentAmount = _uiState.value.amount
        val newAmount = when (char) {
            in "0".."9" -> {
                if (currentAmount == "0") char
                else currentAmount + char
            }
            "." -> {
                if (!currentAmount.contains(".")) {
                    if (currentAmount.isEmpty()) "0." else "$currentAmount."
                } else {
                    currentAmount
                }
            }
            "<" -> {
                if (currentAmount.length > 1) currentAmount.dropLast(1)
                else "0"
            }
            else -> currentAmount

        }
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun saveTransaction():Boolean {
        val currentState = _uiState.value
        val amountToSave: Double = currentState.amount.toDoubleOrNull() ?: 0.0

        if(currentState.category.name == "DEFAULT"){
            _uiState.update { it.copy(errorMessage = "Please select a category") }
            return false
        }
        if(amountToSave == 0.0){
            _uiState.update { it.copy(errorMessage = "Amount cannot be zero") }
            return false
        }

        viewModelScope.launch {
            val transaction = Transaction(
                id = currentState.id,
                amount = amountToSave,
                type = currentState.type.name,
                category = currentState.category.name,
                description = currentState.description.ifBlank { null },
                dateTime = currentState.date.time,
                createdAt = System.currentTimeMillis()
            )
            if (currentState.id == 0L) {
                transactionRepository.insertTransaction(transaction)
            } else {
                transactionRepository.updateTransaction(transaction)
            }

            resetState()
        }
        return true
    }

    fun resetState() {
        _uiState.value = TransactionUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        _uiState.update {
            it.copy(
                id = transaction.id,
                amount = transaction.amount.toString(),
                type = TransactionType.valueOf(transaction.type),
                category = TransactionCategory.valueOf(transaction.category),
                date = Date(transaction.dateTime),
                description = transaction.description ?: ""
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateTypeFilter(type: String) {
        _typeFilter.value = type
    }

    fun getMonthlyCategoryData(transactions: List<Transaction>): List<PieData> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        val groupedTotals: Map<String, Float> = transactions
            .filter { it.type == "EXPENSE" && it.dateTime in startOfMonth..endOfMonth }
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount }.toFloat() }

        val sortedCategories = groupedTotals.entries.sortedByDescending { it.value }

        val maxIndividualCategories = 4

        val topCategories = sortedCategories.take(maxIndividualCategories)
        val remainingCategories = sortedCategories.drop(maxIndividualCategories)

        val pieDataList = mutableListOf<PieData>()

        topCategories.forEachIndexed { index, entry ->
            pieDataList.add(
                PieData(
                    label = entry.key,
                    value = entry.value,
                    color = pieChartColors[index % pieChartColors.size]
                )
            )
        }

        if (remainingCategories.isNotEmpty()) {
            val othersTotal = remainingCategories.sumOf { it.value.toDouble() }.toFloat()
            pieDataList.add(
                PieData(
                    label = "OTHERS",
                    value = othersTotal,
                    color = Color(0xFF9E9E9E)
                )
            )
        }

        return pieDataList
    }

    private val pieChartColors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF00BCD4)  // Cyan
    )

    fun getIncomeExpenseData(transactions: List<Transaction>): List<BarGroup> {
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())

        val last6MonthsLabels = (5 downTo 0).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -i)
            monthFormat.format(cal.time)
        }

        val sixMonthsAgoCal = Calendar.getInstance()
        sixMonthsAgoCal.add(Calendar.MONTH, -5)
        sixMonthsAgoCal.set(Calendar.DAY_OF_MONTH, 1)
        sixMonthsAgoCal.set(Calendar.HOUR_OF_DAY, 0)
        val sixMonthsAgo = sixMonthsAgoCal.timeInMillis

        val recentTransactionsGrouped = transactions
            .filter { it.dateTime >= sixMonthsAgo }
            .groupBy { monthFormat.format(Date(it.dateTime)) }

        if (recentTransactionsGrouped.isEmpty()) {
            return emptyList()
        }

        return last6MonthsLabels.map { monthLabel ->
            val monthTransactions = recentTransactionsGrouped[monthLabel] ?: emptyList()

            val totalIncome = monthTransactions
                .filter { it.type == "INCOME" }
                .sumOf { it.amount }.toFloat()

            val totalExpense = monthTransactions
                .filter { it.type == "EXPENSE" }
                .sumOf { it.amount }.toFloat()

            BarGroup(
                monthLabel,
                listOf(totalIncome, totalExpense),
                colors = listOf(
                    ChartyColor.Solid(Color(0xFF4CAF50)),
                    ChartyColor.Solid(Color(0xFFF44336))
                )
            )
        }
    }

    fun getSixMonthData(transactions: List<Transaction>): List<BarData> {
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

        val last6MonthsLabels = (5 downTo 0).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -i)
            monthFormat.format(cal.time)
        }

        val sixMonthsAgoCal = Calendar.getInstance()
        sixMonthsAgoCal.add(Calendar.MONTH, -5)
        sixMonthsAgoCal.set(Calendar.DAY_OF_MONTH, 1)
        sixMonthsAgoCal.set(Calendar.HOUR_OF_DAY, 0)
        val sixMonthsAgo = sixMonthsAgoCal.timeInMillis

        val groupedExpenses = transactions
            .filter { it.type == "EXPENSE" && it.dateTime >= sixMonthsAgo }
            .groupBy { monthFormat.format(Date(it.dateTime)) }
            .mapValues { (_, list) -> list.sumOf { it.amount }.toFloat() }

        if (groupedExpenses.isEmpty()) {
            return emptyList()
        }

        return last6MonthsLabels.map { monthLabel ->
            BarData(monthLabel, groupedExpenses[monthLabel] ?: 0f)
        }
    }

    fun getLastWeekData(transactions: List<Transaction>): List<LineData>{
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        val last7Days = (6 downTo 0).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            dateFormat.format(cal.time) to cal
        }

        val startOf7Days = last7Days.first().second.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val recentExpensesGrouped = transactions
            .filter { it.type == "EXPENSE" && it.dateTime >= startOf7Days }
            .groupBy { dateFormat.format(Date(it.dateTime)) }

        if (recentExpensesGrouped.isEmpty()) {
            return emptyList()
        }

        return last7Days.map { (dateLabel, _) ->
            val dailyTotal = recentExpensesGrouped[dateLabel]
                ?.sumOf { it.amount }
                ?.toFloat() ?: 0f

            LineData(
                dateLabel, dailyTotal
            )
        }
    }
}