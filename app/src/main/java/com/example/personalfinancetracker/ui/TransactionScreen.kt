package com.example.personalfinancetracker.ui

import android.provider.SyncStateContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onDelete: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    typeFilter: String,
    onTypeFilterChange: (String) -> Unit,
) {
    if(transactions.isEmpty()){
        Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Click + to add a transaction")
        }
    }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState()

     val reversedTransactions = transactions.reversed()

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = typeFilter == "ALL",
                    onClick = { onTypeFilterChange("ALL") },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = typeFilter == "INCOME",
                    onClick = { onTypeFilterChange("INCOME") },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = typeFilter == "EXPENSE",
                    onClick = { onTypeFilterChange("EXPENSE") },
                    label = { Text("Expense") }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(reversedTransactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { selectedTransaction = transaction }
                    )
                }
            }
        }
    }

    if(selectedTransaction != null){
        ModalBottomSheet(onDismissRequest = {selectedTransaction = null},
            sheetState = sheetState
        ) {
            TransactionSheetContent(
                transaction = selectedTransaction!!,
                onDelete = {
                    selectedTransaction = null
                    onDelete(it)
                },
                onEdit = onEdit,
            )
        }
    }
}

@Composable
fun TransactionSheetContent(
    transaction: Transaction,
    onDelete: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "₹${transaction.amount}", style = MaterialTheme.typography.displayLarge)
        Text(text = transaction.category, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (!transaction.description.isNullOrBlank()) {
            Text(text = "Note: ${transaction.description}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = { onEdit(transaction) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }

            Button(
                onClick = { onDelete(transaction) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(Modifier.width(8.dp))
                Text("Delete")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = transaction.category, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(2.dp))
            val date = Date(transaction.dateTime)
            val formatter = SimpleDateFormat("MMMM d, yy", Locale.getDefault())
            Text(text = formatter.format(date), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(2.dp))
            Text(text = transaction.type, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(2.dp))
        }

        Text(
            text = "₹${transaction.amount}",
            style = MaterialTheme.typography.titleLarge,
            color = if (transaction.type == "INCOME") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}