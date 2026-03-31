package com.fleet.ledger.feature.maintenance

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
import com.fleet.ledger.core.domain.model.Maintenance
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.ui.components.EmptyState
import com.fleet.ledger.ui.components.EnterpriseCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel,
    modifier: Modifier = Modifier
) {
    val maintenances by viewModel.maintenances.collectAsState()
    val upcomingMaintenances by viewModel.upcomingMaintenances.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add maintenance */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Bakım Ekle")
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
                    text = "Bakım Takibi",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (upcomingMaintenances.isNotEmpty()) {
                item {
                    UpcomingMaintenanceCard(maintenances = upcomingMaintenances)
                }
            }

            if (maintenances.isEmpty()) {
                item {
                    EmptyState(
                        message = "Henüz bakım kaydı yok",
                        icon = Icons.Default.Build
                    )
                }
            } else {
                items(maintenances) { maintenance ->
                    MaintenanceCard(maintenance = maintenance)
                }
            }
        }
    }
}

@Composable
private fun UpcomingMaintenanceCard(
    maintenances: List<Maintenance>,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = com.fleet.ledger.ui.theme.Warning,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Yaklaşan Bakımlar",
                    style = MaterialTheme.typography.titleMedium,
                    color = com.fleet.ledger.ui.theme.Warning
                )
            }
            maintenances.take(3).forEach { maintenance ->
                Text(
                    text = "${maintenance.type.label} - ${maintenance.nextMaintenanceDate?.formatDate() ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    maintenance: Maintenance,
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
                        text = maintenance.type.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = maintenance.date.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = maintenance.cost.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (maintenance.description.isNotBlank()) {
                Text(
                    text = maintenance.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (maintenance.mileage > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Km:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${maintenance.mileage} km",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (maintenance.nextMaintenanceKm != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sonraki Bakım:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${maintenance.nextMaintenanceKm} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = com.fleet.ledger.ui.theme.Warning
                    )
                }
            }
        }
    }
}
