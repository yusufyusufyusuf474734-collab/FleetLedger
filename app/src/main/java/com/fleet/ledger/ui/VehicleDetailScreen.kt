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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.Trip
import com.fleet.ledger.data.Vehicle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(vehicleId: Long, vm: MainViewModel, onBack: () -> Unit) {
    val vehicles by vm.vehicles.collectAsState()
    val vehicle = vehicles.find { it.id == vehicleId } ?: return
    val trips by vm.tripsFor(vehicleId).collectAsState(emptyList())
    var showAddTrip by remember { mutableStateOf(false) }
    var editTrip by remember { mutableStateOf<Trip?>(null) }
    var deleteTrip by remember { mutableStateOf<Trip?>(null) }

    val totalIncome  = trips.sumOf { it.income }
    val totalExpense = trips.sumOf { it.totalExpense }
    val net          = totalIncome - totalExpense

    if (showAddTrip || editTrip != null) {
        TripFormDialog(
            vehicleId = vehicleId,
            existing = editTrip,
            onDismiss = { showAddTrip = false; editTrip = null },
            onSave = { trip ->
                if (editTrip != null) vm.updateTrip(trip) else vm.addTrip(trip)
                showAddTrip = false; editTrip = null
            }
        )
    }

    deleteTrip?.let { t ->
        AlertDialog(
            onDismissRequest = { deleteTrip = null },
            title = { Text("Seferi Sil") },
            text = { Text("Bu sefer kaydı silinecek. Emin misiniz?") },
            confirmButton = {
                Button(onClick = { vm.deleteTrip(t); deleteTrip = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deleteTrip = null }) { Text("İptal") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(vehicle.plate, fontWeight = FontWeight.Bold)
                        Text(vehicle.name, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTrip = true }) {
                Icon(Icons.Default.Add, "Sefer Ekle")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Özet
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${trips.size} Sefer", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryChip("Gelir", totalIncome, Color(0xFF10B981), Modifier.weight(1f))
                            SummaryChip("Gider", totalExpense, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                            SummaryChip("Net", net,
                                if (net >= 0) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                Modifier.weight(1f))
                        }
                    }
                }
            }

            if (trips.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Receipt, null, modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            Spacer(Modifier.height(10.dp))
                            Text("Sefer kaydı yok",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            } else {
                items(trips, key = { it.id }) { trip ->
                    TripCard(trip,
                        onEdit = { editTrip = trip },
                        onDelete = { deleteTrip = trip })
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val net = trip.netProfit
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(sdf.format(Date(trip.date)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (trip.description.isNotBlank()) {
                        Text(trip.description, style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium)
                    }
                }
                Text(net.fmt(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (net >= 0) Color(0xFF10B981) else MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TripAmountItem("Gelir", trip.income, Color(0xFF10B981))
                TripAmountItem("Yakıt", trip.fuelCost, MaterialTheme.colorScheme.error)
                TripAmountItem("Köprü", trip.bridgeCost, MaterialTheme.colorScheme.error)
                TripAmountItem("Otoban", trip.highwayCost, MaterialTheme.colorScheme.error)
                TripAmountItem("Şoför", trip.driverFee, MaterialTheme.colorScheme.error)
                if (trip.otherCost > 0)
                    TripAmountItem("Diğer", trip.otherCost, MaterialTheme.colorScheme.error)
            }
            if (trip.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(trip.note, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TripAmountItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(amount.fmt(), style = MaterialTheme.typography.labelMedium, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
