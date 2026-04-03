package com.example.personalfinancetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalfinancetracker.data.TransactionsRepository

class DashBoardViewModelFactory(
    private val repository: TransactionsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashBoardViewModel(repository) as T
    }
}