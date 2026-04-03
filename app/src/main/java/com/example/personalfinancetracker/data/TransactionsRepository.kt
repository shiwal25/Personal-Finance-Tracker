package com.example.personalfinancetracker.data

import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getAllTransactionsStream(): Flow<List<Transaction>>

//    fun getTransactionStream(id: Int): Flow<Transaction?>

    fun getTotalIncome(): Flow<Double?>

    fun getTotalExpense(): Flow<Double?>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)
}