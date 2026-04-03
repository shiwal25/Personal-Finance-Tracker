package com.example.personalfinancetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.DashboardUiState
import com.example.personalfinancetracker.data.TransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DashBoardViewModel (private val repository: TransactionsRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        combine(
            repository.getTotalIncome(),
            repository.getTotalExpense()
        ) { income, expense ->
            val incomeValue = income ?: 0.0
            val expenseValue = expense ?: 0.0

            DashboardUiState(
                totalIncome = incomeValue,
                totalExpense = expenseValue,
                netBalance = incomeValue - expenseValue
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )
}