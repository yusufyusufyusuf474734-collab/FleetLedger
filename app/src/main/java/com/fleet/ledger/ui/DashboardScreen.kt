package com.fleet.ledger.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.Vehicle
import com.fleet.ledger.data.VehicleSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: MainViewModel, onVehicleClick: (Long) -> Unit) {
    val vehicles by vm.vehicles.collectAsState()
    val summaries by vm.summaries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddVehicleDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { plate, name -> vm.addVehicle(plate, name); showAddDialog = false }
        )
    }

    // Genel toplam
    val totalIncome  = summaries.sumOf { it.totalIncome }
    val totalExpense = summaries.sumOf { it.totalExpense }
    val totalNet     = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Araç Hesap Takip", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Araç Ekle")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Genel özet
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Genel Özet", style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryChip("Toplam Gelir", totalIncome, Color(0xFF10B981), Modifier.weight(1f))
                            SummaryChip("Toplam Gider", totalExpense, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                            SummaryChip("Net Kar", totalNet,
                                if (totalNet >= 0) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Text("Araçlar (${vehicles.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (vehicles.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DirectionsBus, null,
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            Spacer(Modifier.height(10.dp))
                            Text("Henüz araç yok",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            Text("+ butonuna tıklayarak araç ekleyin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                        }
                    }
                }
            } else {
                items(vehicles, key = { it.id }) { vehicle ->
                    val summary = summaries.find { it.vehicleId == vehicle.id }
                    VehicleCard(vehicle, summary, onClick = { onVehicleClick(vehicle.id) })
                }
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, summary: VehicleSummary?, onClick: () -> Unit) {
    val net = summary?.netProfit ?: 0.0
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
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
                if (summary != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Gelir: ${summary.totalIncome.fmt()}  ·  Gider: ${summary.totalExpense.fmt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(net.fmt(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (net >= 0) Color(0xFF10B981) else MaterialTheme.colorScheme.error)
                Text("Net", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun SummaryChip(label: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f), modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount.fmt(), style = MaterialTheme.typography.labelLarge,
                color = color, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AddVehicleDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var plate by remember { mutableStateOf("") }
    var name  by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Araç Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = plate, onValueChange = { plate = it },
                    label = { Text("Plaka") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Şoför / Araç Adı") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { if (plate.isNotBlank()) onAdd(plate, name) },
                enabled = plate.isNotBlank()) { Text("Ekle") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

fun Double.fmt(): String = "₺%,.0f".format(this)
