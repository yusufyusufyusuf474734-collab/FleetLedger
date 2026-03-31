package com.fleet.ledger.feature.fuel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.core.domain.model.FuelRecord
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.ui.components.EmptyState
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.Section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelManagementScreen(
    viewModel: FuelManagementViewModel,
    modifier: Modifier = Modifier
) {
    val fuelRecords by viewModel.fuelRecords.collectAsState()
    val statistics by viewModel.statistics.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add fuel record */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yakıt Ekle")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Yakıt Yönetimi",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Section(title = "İstatistikler") {
                    FuelStatisticsCard(statistics = statistics)
                }
            }

            if (fuelRecords.isEmpty()) {
                item {
                    EmptyState(
                        message = "Henüz yakıt kaydı yok",
                        icon = Icons.Default.LocalGasStation
                    )
                }
            } else {
                items(fuelRecords) { record ->
                    FuelRecordCard(record = record)
                }
            }
        }
    }
}

@Composable
private fun FuelStatisticsCard(
    statistics: FuelStatistics,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Ort. Tüketim",
                    value = String.format("%.2f km/L", statistics.averageConsumption),
                    icon = Icons.Default.Speed
                )
                StatItem(
                    label = "Toplam Maliyet",
                    value = statistics.totalCost.formatCurrency(),
                    icon = Icons.Default.AttachMoney
                )
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Toplam Litre",
                    value = String.format("%.1f L", statistics.totalLiters),
                    icon = Icons.Default.LocalGasStation
                )
                StatItem(
                    label = "Ort. Fiyat",
                    value = String.format("%.2f ₺/L", statistics.averagePrice),
                    icon = Icons.Default.TrendingUp
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FuelRecordCard(
    record: FuelRecord,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${record.liters} L - ${record.fuelType}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = record.date.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = record.totalCost.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Litre Fiyatı:", style = MaterialTheme.typography.bodySmall)
                Text(
                    String.format("%.2f ₺/L", record.pricePerLiter),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Km:", style = MaterialTheme.typography.bodySmall)
                Text(
                    "${record.mileage} km",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (record.kmPerLiter > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tüketim:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        String.format("%.2f km/L", record.kmPerLiter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (record.station.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("İstasyon:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        record.station,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
