package com.fleet.ledger.feature.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.ui.components.*
import com.fleet.ledger.ui.theme.*

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val incomeData by viewModel.incomeData.collectAsState()
    val expenseBreakdown by viewModel.expenseBreakdown.collectAsState()
    val vehiclePerformance by viewModel.vehiclePerformance.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Analitik & Grafikler",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Section(title = "Gelir Trendi") {
                EnterpriseCard {
                    LineChart(
                        data = incomeData,
                        modifier = Modifier.padding(16.dp),
                        lineColor = Primary600
                    )
                }
            }
        }

        item {
            Section(title = "Gider Dağılımı") {
                EnterpriseCard {
                    PieChart(
                        data = expenseBreakdown,
                        colors = listOf(
                            Error,
                            Warning,
                            Primary600,
                            Secondary500,
                            Info
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item {
            Section(title = "Araç Performansı") {
                EnterpriseCard {
                    BarChart(
                        data = vehiclePerformance,
                        modifier = Modifier.padding(16.dp),
                        barColor = Secondary500
                    )
                }
            }
        }
    }
}
