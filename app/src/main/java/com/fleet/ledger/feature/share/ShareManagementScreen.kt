package com.fleet.ledger.feature.share

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
import com.fleet.ledger.core.domain.model.VehiclePartnerShare
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.Section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareManagementScreen(
    viewModel: ShareManagementViewModel,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val partners by viewModel.partners.collectAsState()
    val shares by viewModel.shares.collectAsState()
    val profitDistribution by viewModel.profitDistribution.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add share */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Hisse Ekle")
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
                    text = "Hisse & Kar Paylaşımı",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Section(title = "Kar Dağılımı") {
                    ProfitDistributionCard(distribution = profitDistribution)
                }
            }

            item {
                Section(title = "Araç Hisseleri") {}
            }

            items(shares) { share ->
                ShareCard(share = share)
            }
        }
    }
}

@Composable
private fun ProfitDistributionCard(
    distribution: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bu Ay Kar Dağılımı",
                style = MaterialTheme.typography.titleMedium
            )
            Divider()
            
            distribution.forEach { (partnerName, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = partnerName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = amount.formatCurrency(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareCard(
    share: VehiclePartnerShare,
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
                        text = share.vehiclePlate,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = share.partnerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "%${share.sharePercent.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toplam Kar:", style = MaterialTheme.typography.bodySmall)
                Text(
                    share.totalProfit.formatCurrency(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hisse Payı:", style = MaterialTheme.typography.bodySmall)
                Text(
                    share.shareAmount.formatCurrency(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
