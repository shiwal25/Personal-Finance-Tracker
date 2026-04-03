package com.example.personalfinancetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    balance: Double,
    income: Double,
    expense: Double,
    modifier:Modifier = Modifier
) {
    BalanceSection(balance, income, expense, modifier)
}

@Composable
fun BalanceSection(
    balance: Double,
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Current Balance",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Text(
            text = "₹${String.format("%,.2f", balance)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BalanceCard(
                title = "Income",
                amount = income,
                backgroundColor = Color(0xFF4A6CF7),
                modifier = Modifier.weight(1f)
            )
            BalanceCard(
                title = "Expenses",
                amount = expense,
                backgroundColor = Color(0xFFFF6B6B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BalanceCard(
    title: String,
    amount: Double,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "₹${String.format("%,.0f", amount)}",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}