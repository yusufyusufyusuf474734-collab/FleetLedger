package com.fleet.ledger.feature.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.Section
import java.util.*

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.monthlySummary.collectAsState()
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Aylık Raporlar",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            MonthYearSelector(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onMonthChange = { selectedMonth = it },
                onYearChange = { selectedYear = it }
            )
        }

        item {
            Section(title = "Gelir-Gider Özeti") {
                FinancialSummaryCard(
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    netProfit = summary.netProfit
                )
            }
        }

        item {
            Section(title = "Gider Dağılımı") {
                ExpenseBreakdownCard(
                    fuelCost = summary.fuelCost,
                    bridgeCost = summary.bridgeCost,
                    highwayCost = summary.highwayCost,
                    driverFee = summary.driverFee,
                    otherCost = summary.otherCost
                )
            }
        }

        item {
            Section(title = "Araç Bazlı Performans") {
                VehiclePerformanceCard(
                    tripCount = summary.tripCount,
                    averageProfit = summary.averageProfit
                )
            }
        }
    }
}

@Composable
private fun MonthYearSelector(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    EnterpriseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rapor Dönemi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${months[selectedMonth - 1]} $selectedYear",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { 
                    if (selectedMonth == 1) {
                        onMonthChange(12)
                        onYearChange(selectedYear - 1)
                    } else {
                        onMonthChange(selectedMonth - 1)
                    }
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki Ay")
                }
                IconButton(onClick = {
                    if (selectedMonth == 12) {
                        onMonthChange(1)
                        onYearChange(selectedYear + 1)
                    } else {
                        onMonthChange(selectedMonth + 1)
                    }
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki Ay")
                }
            }
        }
    }
}

@Composable
private fun FinancialSummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    netProfit: Double,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryRow(
                label = "Toplam Gelir",
                value = totalIncome.formatCurrency(),
                color = MaterialTheme.colorScheme.primary
            )
            Divider()
            SummaryRow(
                label = "Toplam Gider",
                value = totalExpense.formatCurrency(),
                color = MaterialTheme.colorScheme.error
            )
            Divider()
            SummaryRow(
                label = "Net Kar",
                value = netProfit.formatCurrency(),
                color = if (netProfit >= 0) MaterialTheme.colorScheme.secondary 
                       else MaterialTheme.colorScheme.error,
                isHighlighted = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlighted) MaterialTheme.typography.titleMedium 
                   else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.titleLarge 
                   else MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}

@Composable
private fun ExpenseBreakdownCard(
    fuelCost: Double,
    bridgeCost: Double,
    highwayCost: Double,
    driverFee: Double,
    otherCost: Double,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpenseItem("Yakıt", fuelCost, Icons.Default.LocalGasStation)
            ExpenseItem("Köprü", bridgeCost, Icons.Default.AccountBalance)
            ExpenseItem("Otoyol", highwayCost, Icons.Default.Route)
            ExpenseItem("Şoför Ücreti", driverFee, Icons.Default.Person)
            ExpenseItem("Diğer", otherCost, Icons.Default.MoreHoriz)
        }
    }
}

@Composable
private fun ExpenseItem(
    label: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = amount.formatCurrency(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VehiclePerformanceCard(
    tripCount: Int,
    averageProfit: Double,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PerformanceMetric(
                label = "Toplam Sefer",
                value = tripCount.toString(),
                icon = Icons.Default.DirectionsBus
            )
            PerformanceMetric(
                label = "Ortalama Kar",
                value = averageProfit.formatCurrency(),
                icon = Icons.Default.TrendingUp
            )
        }
    }
}

@Composable
private fun PerformanceMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
