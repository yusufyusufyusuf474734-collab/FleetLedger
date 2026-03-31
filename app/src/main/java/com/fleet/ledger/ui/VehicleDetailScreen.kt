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
import com.fleet.ledger.data.Document
import com.fleet.ledger.data.DocumentType
import com.fleet.ledger.data.Trip
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(vehicleId: Long, vm: MainViewModel, onBack: () -> Unit) {
    val vehicle = vm.vehicleById(vehicleId) ?: return
    val trips by vm.tripsFor(vehicleId).collectAsState(emptyList())
    val docs  by vm.documentsFor(vehicleId).collectAsState(emptyList())
    var tab by remember { mutableIntStateOf(0) }

    val totalIncome  = trips.sumOf { it.income }
    val totalExpense = trips.sumOf { it.totalExpense }
    val net          = totalIncome - totalExpense

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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Özet
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryTile("Gelir", totalIncome, Green500, Modifier.weight(1f))
                SummaryTile("Gider", totalExpense, Red500, Modifier.weight(1f))
                SummaryTile("Net", net, if (net >= 0) Green500 else Red500, Modifier.weight(1f))
            }

            // Tab
            TabRow(selectedTabIndex = tab,
                containerColor = MaterialTheme.colorScheme.surface) {
                Tab(selected = tab == 0, onClick = { tab = 0 },
                    text = { Text("Seferler (${trips.size})") })
                Tab(selected = tab == 1, onClick = { tab = 1 },
                    text = { Text("Belgeler (${docs.size})") })
            }

            when (tab) {
                0 -> TripsTab(trips, vm, vehicleId)
                1 -> DocumentsTab(docs, vm, vehicleId)
            }
        }
    }
}

@Composable
fun TripsTab(trips: List<Trip>, vm: MainViewModel, vehicleId: Long) {
    var showAdd by remember { mutableStateOf(false) }
    var editTrip by remember { mutableStateOf<Trip?>(null) }
    var deleteTrip by remember { mutableStateOf<Trip?>(null) }

    if (showAdd || editTrip != null) {
        TripFormDialog(vehicleId, editTrip,
            onDismiss = { showAdd = false; editTrip = null },
            onSave = { t -> if (editTrip != null) vm.updateTrip(t) else vm.addTrip(t)
                showAdd = false; editTrip = null })
    }
    deleteTrip?.let { t ->
        AlertDialog(onDismissRequest = { deleteTrip = null },
            title = { Text("Seferi Sil") },
            text = { Text("Bu sefer kaydı silinecek.") },
            confirmButton = {
                Button(onClick = { vm.deleteTrip(t); deleteTrip = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deleteTrip = null }) { Text("İptal") } })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (trips.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Receipt, null, modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                    Spacer(Modifier.height(8.dp))
                    Text("Sefer kaydı yok",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trips, key = { it.id }) { trip ->
                    TripCard(trip, onEdit = { editTrip = trip }, onDelete = { deleteTrip = trip })
                }
            }
        }
        FloatingActionButton(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, "Sefer Ekle") }
    }
}

@Composable
fun TripCard(trip: Trip, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val net = trip.netProfit
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(sdf.format(Date(trip.date)), style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (trip.description.isNotBlank())
                        Text(trip.description, style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium)
                }
                Text(net.tl(), style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (net >= 0) Green500 else Red500)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TripAmt("Gelir", trip.income, Green500)
                TripAmt("Yakıt", trip.fuelCost, Red500)
                TripAmt("Köprü", trip.bridgeCost, Red500)
                TripAmt("Otoban", trip.highwayCost, Red500)
                TripAmt("Şoför", trip.driverFee, Red500)
                if (trip.otherCost > 0) TripAmt("Diğer", trip.otherCost, Red500)
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
fun TripAmt(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(amount.tl(), style = MaterialTheme.typography.labelMedium, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Belgeler sekmesi ──────────────────────────────────────────────────────────

@Composable
fun DocumentsTab(docs: List<Document>, vm: MainViewModel, vehicleId: Long) {
    var showAdd by remember { mutableStateOf(false) }
    var editDoc by remember { mutableStateOf<Document?>(null) }
    var deleteDoc by remember { mutableStateOf<Document?>(null) }

    if (showAdd || editDoc != null) {
        DocumentFormDialog(vehicleId, editDoc,
            onDismiss = { showAdd = false; editDoc = null },
            onSave = { d -> if (editDoc != null) vm.updateDocument(d) else vm.addDocument(d)
                showAdd = false; editDoc = null })
    }
    deleteDoc?.let { d ->
        AlertDialog(onDismissRequest = { deleteDoc = null },
            title = { Text("Belgeyi Sil") },
            text = { Text("${d.type.label} belgesi silinecek.") },
            confirmButton = {
                Button(onClick = { vm.deleteDocument(d); deleteDoc = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deleteDoc = null }) { Text("İptal") } })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (docs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Description, null, modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                    Spacer(Modifier.height(8.dp))
                    Text("Belge eklenmemiş",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(docs, key = { it.id }) { doc ->
                    DocumentCard(doc, onEdit = { editDoc = doc }, onDelete = { deleteDoc = doc })
                }
            }
        }
        FloatingActionButton(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, "Belge Ekle") }
    }
}

@Composable
fun DocumentCard(doc: Document, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val daysLeft = doc.expiryDate?.let {
        TimeUnit.MILLISECONDS.toDays(it - System.currentTimeMillis())
    }
    val statusColor = when {
        daysLeft == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysLeft < 0    -> Red500
        daysLeft <= 7   -> Red500
        daysLeft <= 30  -> Amber500
        else            -> Green500
    }
    val statusText = when {
        daysLeft == null -> ""
        daysLeft < 0    -> "Süresi doldu"
        daysLeft == 0L  -> "Bugün bitiyor"
        else            -> "$daysLeft gün kaldı"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Description, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(doc.type.label, style = MaterialTheme.typography.titleSmall)
                    if (doc.title.isNotBlank())
                        Text(doc.title, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (statusText.isNotEmpty()) {
                    Surface(shape = MaterialTheme.shapes.extraSmall,
                        color = statusColor.copy(alpha = 0.15f)) {
                        Text(statusText, style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (doc.company.isNotBlank()) DocInfo("Şirket", doc.company)
                if (doc.policyNo.isNotBlank()) DocInfo("Poliçe No", doc.policyNo)
                doc.startDate?.let { DocInfo("Başlangıç", sdf.format(Date(it))) }
                doc.expiryDate?.let { DocInfo("Bitiş", sdf.format(Date(it)), statusColor) }
                if (doc.amount > 0) DocInfo("Prim", doc.amount.tl())
            }
            if (doc.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(doc.note, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DocInfo(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color)
    }
}
