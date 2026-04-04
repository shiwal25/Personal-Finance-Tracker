package com.example.personalfinancetracker.data

import java.util.Date


data class TransactionUiState (
    val id: Long = 0,
    val amount: String = "0",
    val type:TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.DEFAULT,
    val date: Date = Date(),
    val description: String = "",
    val errorMessage: String? = null
)

enum class TransactionType () {
    INCOME, EXPENSE
}

enum class TransactionCategory(val displayName: String) {
    FOOD("Food & Dining"),
    TRANSPORT("Transportation"),
    HOUSING("Rent & Housing"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health & Fitness"),
    SALARY("Salary"),
    PERSONAL("Personal Care"),
    EDUCATION("Education"),
    BILLS("Bills & Utilities"),
    OTHER("Other"),
    DEFAULT("Select Category")
}