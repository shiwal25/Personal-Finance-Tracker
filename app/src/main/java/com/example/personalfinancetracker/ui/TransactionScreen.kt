package com.example.personalfinancetracker.ui

import androidx.compose.runtime.Composable
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionUiState

@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    onTransactionClick: (TransactionUiState) -> Unit,
){

}