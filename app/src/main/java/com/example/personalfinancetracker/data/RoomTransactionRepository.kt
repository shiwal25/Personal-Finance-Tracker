package com.example.personalfinancetracker.data

import kotlinx.coroutines.flow.Flow

class RoomTransactionRepository(private val transactionDAO: TransactionDAO) : TransactionsRepository {
    override fun getAllTransactionsStream(): Flow<List<Transaction>> {
        return transactionDAO.getAllTransactions()
    }

//    override fun getTransactionStream(id: Int): Flow<Transaction?> {
//        return transactionDAO.getTransaction(id)
//    }

    override fun getTotalIncome(): Flow<Double?> {
        return transactionDAO.getTotalIncome()
    }

    override fun getTotalExpense(): Flow<Double?> {
        return transactionDAO.getTotalExpense()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDAO.insert(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDAO.delete(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDAO.update(transaction)
    }
}