package com.example.personalfinancetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionUiState
import com.example.personalfinancetracker.data.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

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
                amount = amountToSave,
                type = currentState.type.name,
                category = currentState.category.name,
                description = currentState.description.ifBlank { null },
                dateTime = currentState.date.time,
            )
            transactionRepository.insertTransaction(transaction)
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
}