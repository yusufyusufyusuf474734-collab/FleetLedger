package com.fleet.ledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(vm: MainViewModel, onVehicleClick: (Long) -> Unit) {
    val vehicles by vm.vehicles.collectAsState()
    val summaries by vm.summaries.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var deleteVehicle by remember { mutableStateOf<Vehicle?>(null) }

    if (showAdd || editVehicle != null) {
        VehicleFormDialog(
            existing = editVehicle,
            onDismiss = { showAdd = false; editVehicle = null },
            onSave = { plate, name, brand, year ->
                if (editVehicle != null)
                    vm.updateVehicle(editVehicle!!.copy(plate = plate.uppercase(), name = name, brand = brand, year = year))
                else
                    vm.addVehicle(plate, name, brand, year)
                showAdd = false; editVehicle = null
            }
        )
    }

    deleteVehicle?.let { v ->
        AlertDialog(
            onDismissRequest = { deleteVehicle = null },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Aracı Sil") },
            text = { Text("${v.plate} plakalı araç ve tüm sefer/belge kayıtları silinecek.") },
            confirmButton = {
                Button(onClick = { vm.deleteVehicle(v); deleteVehicle = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deleteVehicle = null }) { Text("İptal") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, "Araç Ekle")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (vehicles.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                    Spacer(Modifier.height(12.dp))
                    Text("Araç eklenmemiş",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(4.dp))
                    Text("+ butonuna tıklayarak araç ekleyin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vehicles, key = { it.id }) { vehicle ->
                    val summary = summaries.find { it.vehicleId == vehicle.id }
                    VehicleListItem(
                        vehicle = vehicle,
                        netProfit = summary?.netProfit ?: 0.0,
                        onClick = { onVehicleClick(vehicle.id) },
                        onEdit = { editVehicle = vehicle },
                        onDelete = { deleteVehicle = vehicle }
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(
    vehicle: Vehicle,
    netProfit: Double,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBus, null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.plate, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                Text(vehicle.name, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (vehicle.brand.isNotBlank() || vehicle.year > 0) {
                    Text("${vehicle.brand} ${if (vehicle.year > 0) vehicle.year.toString() else ""}".trim(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(netProfit.tl(), style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (netProfit >= 0) Green500 else Red500)
                Text("Net", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(4.dp))
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun VehicleFormDialog(
    existing: Vehicle?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> Unit
) {
    var plate  by remember { mutableStateOf(existing?.plate ?: "") }
    var name   by remember { mutableStateOf(existing?.name ?: "") }
    var brand  by remember { mutableStateOf(existing?.brand ?: "") }
    var year   by remember { mutableStateOf(existing?.year?.takeIf { it > 0 }?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Araç Düzenle" else "Araç Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = plate, onValueChange = { plate = it },
                    label = { Text("Plaka *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Şoför / Araç Adı *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = brand, onValueChange = { brand = it },
                    label = { Text("Marka / Model") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = year, onValueChange = { year = it },
                    label = { Text("Model Yılı") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (plate.isNotBlank() && name.isNotBlank())
                    onSave(plate, name, brand, year.toIntOrNull() ?: 0) },
                enabled = plate.isNotBlank() && name.isNotBlank()
            ) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
