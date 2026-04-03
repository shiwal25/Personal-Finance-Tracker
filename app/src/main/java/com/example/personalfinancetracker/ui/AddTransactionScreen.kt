package com.example.personalfinancetracker.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalfinancetracker.data.TransactionCategory
import com.example.personalfinancetracker.data.TransactionType
import com.example.personalfinancetracker.data.TransactionUiState

@Composable
fun AddTransactionScreen(
    uiState: TransactionUiState,
    onValueChange: (TransactionUiState) -> Unit,
    onNumberClick: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current

    var showCategoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
            onClearError()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel"
                )
                Text("CANCEL", fontSize = 14.sp)
            }
            TextButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save"
                )
                Text("Save", fontSize = 14.sp)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TransactionType.entries.forEach { type ->
                val selected = uiState.type == type
                FilterChip(
                    selected = selected,
                    onClick = { onValueChange(uiState.copy(type = type)) },
                    label = { Text(type.name, fontSize = 14.sp ) },
                    leadingIcon = if (selected) {
                        { Icon(Icons.Default.CheckCircle, null) }
                    } else null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Label, contentDescription = null, )
                    Text(uiState.category.displayName)
                }
            }
        }

        TextField(
            value = uiState.description,
            onValueChange = { onValueChange(uiState.copy(description = it)) },
            placeholder = { Text("Add notes") },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
        )

        Text(
            text = uiState.amount,
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        )
        CalculatorGrid(onNumberClick = onNumberClick)
    }

    if (showCategoryDialog) {
        CategorySelectionDialog(
            categories = TransactionCategory.entries.filter { it.name != "DEFAULT" },
            currentCategory = uiState.category,
            onCategorySelected = { selectedCategory ->
                onValueChange(uiState.copy(category = selectedCategory))
                showCategoryDialog = false
            },
            onDismissRequest = {
                showCategoryDialog = false
            }
        )
    }
}

@Composable
fun CalculatorGrid(onNumberClick: (String) -> Unit) {
    val buttons = listOf(
        listOf("7", "8", "9"),
        listOf("4", "5", "6"),
        listOf("1", "2", "3"),
        listOf("0", ".", "<")
    )

    Column {
        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { char ->
                    Button(
                        onClick = { onNumberClick(char) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f) // Adjust for boxy look
                            .padding(1.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(char, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelectionDialog(
    categories: List<TransactionCategory>,
    currentCategory: TransactionCategory,
    onCategorySelected: (TransactionCategory) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Category") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (category == currentCategory),
                                onClick = { onCategorySelected(category) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (category == currentCategory),
                            onClick = null // Let the Row handle the click
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = category.displayName, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewAddTransactionScreen() {
    val uiState = TransactionUiState(
        type = TransactionType.INCOME,
        category = TransactionCategory.DEFAULT,
        description = "",
    )
    AddTransactionScreen(
        uiState = uiState,
        onValueChange = {},
        onSave = {},
        onCancel = {},
        onNumberClick = {},
        onClearError = {}
    )
}