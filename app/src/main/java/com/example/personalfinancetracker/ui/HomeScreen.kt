package com.example.personalfinancetracker.ui

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.Transaction
import com.himanshoe.charty.bar.ComparisonBarChart
import com.himanshoe.charty.line.AreaChart
import com.himanshoe.charty.line.data.LineData

@Composable
fun HomeScreen(
    balance: Double,
    income: Double,
    expense: Double,
    modifier:Modifier = Modifier,
    lastWeekData: List<LineData>
) {
    Column(
        modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        BalanceSection(balance, income, expense, Modifier)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if(isSystemInDarkTheme()) Color(0xFFCFD8DC) else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Last Week's Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (lastWeekData.isEmpty()) {
                    Text(
                        text = "No expenses recorded this month.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF000000)
                    )
                } else{
                    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                        key(lastWeekData) {
                            AreaChart(
                                { lastWeekData },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 28.dp, end = 12.dp, bottom = 12.dp, top = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        /*TODO Budget adding and showing progress bar*/
    }
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

//@Preview
//@Composable
//fun HomeScreenPreview(balance: Double = 0.0,
//                      income: Double = 0.0,
//                      expense: Double = 0.0,
//                      lastWeekData: List<LineData> = listOf(
//                          LineData("0", 1000.0F),
//                          LineData("1", 1500.0F),
//                          LineData("2", 1200.0F),
//                          LineData("3", 1800.0F),
//                          LineData("4", 1300.0F)
//                      ),
//                      modifier: Modifier = Modifier
//) {
//    HomeScreen(balance, income, expense, modifier, lastWeekData)
//
//}