package com.example.personalfinancetracker.data

import android.content.Context

interface AppContainer {
    val transactionRepository: TransactionsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val transactionRepository: TransactionsRepository by lazy {
        RoomTransactionRepository(TransactionDatabase.getDatabase(context).transactionDAO())
    }
}