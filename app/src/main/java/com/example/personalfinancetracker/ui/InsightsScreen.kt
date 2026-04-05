package com.example.personalfinancetracker.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.ComparisonBarChart
import com.himanshoe.charty.bar.data.BarData
import com.himanshoe.charty.bar.data.BarGroup
import com.himanshoe.charty.pie.PieChart
import com.himanshoe.charty.pie.data.PieData


@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    pieChartData: List<PieData>,
    incomeExpenseData: List<BarGroup>,
    sixMonthsData: List<BarData>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if(isSystemInDarkTheme()) Color(0xFFCFD8DC) else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Expense Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (pieChartData.isEmpty()) {
                    Text(
                        text = "No expenses recorded this month.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF000000)
                    )
                } else{
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        PieChart(
                            modifier = Modifier.size(200.dp),
                            data = { pieChartData },
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        ) {
                            pieChartData.forEach { slice ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    slice.color?.let {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(color = it, shape = CircleShape)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = slice.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF000000)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if(isSystemInDarkTheme()) Color(0xFFCFD8DC) else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cash Flow",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (incomeExpenseData.isEmpty()) {
                    Text(
                        text = "No expenses recorded this month.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF000000)
                    )
                } else{
                    ComparisonBarChart(
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(start = 28.dp, end = 12.dp, bottom = 12.dp, top = 12.dp),
                        data = { incomeExpenseData },

                        )
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if(isSystemInDarkTheme()) Color(0xFFCFD8DC) else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Spending Trend",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (sixMonthsData.isEmpty()) {
                    Text(
                        text = "No expenses recorded this month.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF000000)
                    )
                } else{
                    BarChart(
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(start = 28.dp, end = 12.dp, bottom = 12.dp, top = 12.dp),
                        data = { sixMonthsData },
                    )
                }
            }
        }
    }
}



