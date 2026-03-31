package com.fleet.ledger.feature.vehicle

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
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.feature.trip.TripFormDialog
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.Section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: Long,
    viewModel: VehicleDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle by viewModel.vehicle.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val summary by viewModel.summary.collectAsState()
    var showTripDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle?.plate ?: "Araç Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTripDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Sefer Ekle")
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
                VehicleInfoCard(
                    plate = vehicle?.plate ?: "",
                    brand = vehicle?.brand ?: "",
                    name = vehicle?.name ?: "",
                    year = vehicle?.year ?: 0
                )
            }

            item {
                SummaryCard(
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    netProfit = summary.netProfit,
                    tripCount = summary.tripCount
                )
            }

            item {
                Section(title = "Seferler (${trips.size})") {}
            }

            items(trips) { trip ->
                TripCard(trip = trip)
            }
        }
    }

    if (showTripDialog) {
        TripFormDialog(
            vehicleId = vehicleId,
            onDismiss = { showTripDialog = false },
            onSave = { trip ->
                viewModel.addTrip(trip)
                showTripDialog = false
            }
        )
    }
}

@Composable
private fun VehicleInfoCard(
    plate: String,
    brand: String,
    name: String,
    year: Int,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = plate,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$brand $name",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (year > 0) {
                    Text(
                        text = "Model: $year",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    netProfit: Double,
    tripCount: Int,
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
                text = "Özet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toplam Sefer:", style = MaterialTheme.typography.bodyMedium)
                Text(tripCount.toString(), style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toplam Gelir:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    totalIncome.formatCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toplam Gider:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    totalExpense.formatCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Net Kar:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    netProfit.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (netProfit >= 0) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TripCard(
    trip: Trip,
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
                        text = trip.description.ifBlank { "Sefer" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = trip.date.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = trip.netProfit.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (trip.netProfit >= 0) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error
                )
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gelir:", style = MaterialTheme.typography.bodySmall)
                Text(
                    trip.income.formatCurrency(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gider:", style = MaterialTheme.typography.bodySmall)
                Text(
                    trip.totalExpense.formatCurrency(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
