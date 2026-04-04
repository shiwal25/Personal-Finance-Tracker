package com.example.personalfinancetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionCategory
import com.example.personalfinancetracker.data.TransactionType
import com.example.personalfinancetracker.data.TransactionUiState
import com.example.personalfinancetracker.data.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
}