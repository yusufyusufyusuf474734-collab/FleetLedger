package com.fleet.ledger.feature.vehicle

import androidx.compose.foundation.clickable
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
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.ui.components.EmptyState
import com.fleet.ledger.ui.components.EnterpriseCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    viewModel: VehiclesViewModel,
    onVehicleClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Araç Ekle")
            }
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            if (vehicles.isEmpty()) {
                EmptyState(
                    message = "Henüz araç eklenmemiş.\nYeni araç eklemek için + butonuna tıklayın.",
                    icon = Icons.Default.DirectionsBus
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onClick = { onVehicleClick(vehicle.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddVehicleDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { plate, name, brand, year ->
                viewModel.addVehicle(plate, name, brand, year)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.plate,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${vehicle.brand} ${vehicle.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (vehicle.year > 0) {
                    Text(
                        text = "Model: ${vehicle.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Araç Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("Plaka") },
                    placeholder = { Text("34 ABC 123") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Araç Adı") },
                    placeholder = { Text("Travego") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marka") },
                    placeholder = { Text("Mercedes") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Model Yılı") },
                    placeholder = { Text("2020") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (plate.isNotBlank() && name.isNotBlank()) {
                        onConfirm(plate, name, brand, year.toIntOrNull() ?: 0)
                    }
                },
                enabled = plate.isNotBlank() && name.isNotBlank()
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
