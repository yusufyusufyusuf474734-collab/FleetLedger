package com.fleet.ledger.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.Document
import com.fleet.ledger.data.Trip
import com.fleet.ledger.data.VehiclePartner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(vehicleId: Long, vm: MainViewModel, onBack: () -> Unit) {
    val vehicle = vm.vehicleById(vehicleId) ?: return
    val trips   by vm.tripsFor(vehicleId).collectAsState(emptyList())
    val docs    by vm.documentsFor(vehicleId).collectAsState(emptyList())
    val shares  by vm.sharesFor(vehicleId).collectAsState(emptyList())
    val allPartners by vm.allPartners.collectAsState()
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
            // Özet bar
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniKpi("Gelir", totalIncome, Green500, Modifier.weight(1f))
                    MiniKpi("Gider", totalExpense, Red500, Modifier.weight(1f))
                    MiniKpi("Net", net, if (net >= 0) Green500 else Red500, Modifier.weight(1f))
                }
            }

            TabRow(selectedTabIndex = tab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary) {
                Tab(selected = tab == 0, onClick = { tab = 0 },
                    text = { Text("Seferler") },
                    icon = { Icon(Icons.Default.Receipt, null, modifier = Modifier.size(16.dp)) })
                Tab(selected = tab == 1, onClick = { tab = 1 },
                    text = { Text("Belgeler") },
                    icon = { Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp)) })
                Tab(selected = tab == 2, onClick = { tab = 2 },
                    text = { Text("Ortaklar") },
                    icon = { Icon(Icons.Default.Group, null, modifier = Modifier.size(16.dp)) })
            }

            when (tab) {
                0 -> TripsTab(trips, vm, vehicleId)
                1 -> DocumentsTab(docs, vm, vehicleId)
                2 -> PartnersTab(vehicleId, shares, allPartners, vm)
            }
        }
    }
}

@Composable
fun MiniKpi(label: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small, color = color.copy(alpha = 0.08f), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount.tl(), style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Seferler ──────────────────────────────────────────────────────────────────

@Composable
fun TripsTab(trips: List<Trip>, vm: MainViewModel, vehicleId: Long) {
    var showAdd  by remember { mutableStateOf(false) }
    var editTrip by remember { mutableStateOf<Trip?>(null) }
    var delTrip  by remember { mutableStateOf<Trip?>(null) }

    if (showAdd || editTrip != null) {
        TripFormDialog(vehicleId, editTrip,
            onDismiss = { showAdd = false; editTrip = null },
            onSave = { t -> if (editTrip != null) vm.updateTrip(t) else vm.addTrip(t)
                showAdd = false; editTrip = null })
    }
    delTrip?.let { t ->
        AlertDialog(onDismissRequest = { delTrip = null },
            title = { Text("Seferi Sil") }, text = { Text("Bu sefer kaydı silinecek.") },
            confirmButton = {
                Button(onClick = { vm.deleteTrip(t); delTrip = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { delTrip = null }) { Text("İptal") } })
    }

    Box(Modifier.fillMaxSize()) {
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
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trips, key = { it.id }) { trip ->
                    TripCard(trip, onEdit = { editTrip = trip }, onDelete = { delTrip = trip })
                }
            }
        }
        FloatingActionButton(onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Default.Add, "Sefer Ekle")
        }
    }
}

@Composable
fun TripCard(trip: Trip, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val net = trip.netProfit
    var showReceipt by remember { mutableStateOf(false) }

    ProCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sdf.format(Date(trip.date)), style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (trip.description.isNotBlank())
                    Text(trip.description, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium)
            }
            StatusBadge(net.tl(), if (net >= 0) Green500 else Red500)
            Spacer(Modifier.width(4.dp))
            if (trip.receiptImagePath.isNotBlank()) {
                IconButton(onClick = { showReceipt = !showReceipt }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp),
                        tint = Blue500)
                }
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
        ProDivider()
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
        // Fiş fotoğrafı
        if (showReceipt && trip.receiptImagePath.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = Uri.fromFile(File(trip.receiptImagePath)),
                contentDescription = "Fiş",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Fit
            )
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

// ── Belgeler ──────────────────────────────────────────────────────────────────

@Composable
fun DocumentsTab(docs: List<Document>, vm: MainViewModel, vehicleId: Long) {
    var showAdd by remember { mutableStateOf(false) }
    var editDoc by remember { mutableStateOf<Document?>(null) }
    var delDoc  by remember { mutableStateOf<Document?>(null) }

    if (showAdd || editDoc != null) {
        DocumentFormDialog(vehicleId, editDoc,
            onDismiss = { showAdd = false; editDoc = null },
            onSave = { d -> if (editDoc != null) vm.updateDocument(d) else vm.addDocument(d)
                showAdd = false; editDoc = null })
    }
    delDoc?.let { d ->
        AlertDialog(onDismissRequest = { delDoc = null },
            title = { Text("Belgeyi Sil") }, text = { Text("${d.type.label} belgesi silinecek.") },
            confirmButton = {
                Button(onClick = { vm.deleteDocument(d); delDoc = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { delDoc = null }) { Text("İptal") } })
    }

    Box(Modifier.fillMaxSize()) {
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
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(docs, key = { it.id }) { doc ->
                    DocumentCard(doc, onEdit = { editDoc = doc }, onDelete = { delDoc = doc })
                }
            }
        }
        FloatingActionButton(onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Default.Add, "Belge Ekle")
        }
    }
}

@Composable
fun DocumentCard(doc: Document, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val daysLeft = doc.expiryDate?.let { TimeUnit.MILLISECONDS.toDays(it - System.currentTimeMillis()) }
    val statusColor = when {
        daysLeft == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysLeft < 0    -> Red500
        daysLeft <= 7   -> Red500
        daysLeft <= 30  -> Amber500
        else            -> Green500
    }

    ProCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            daysLeft?.let {
                StatusBadge(if (it < 0) "Doldu" else "$it gün", statusColor)
                Spacer(Modifier.width(4.dp))
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
        ProDivider()
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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

@Composable
fun DocInfo(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

// ── Ortaklar sekmesi ──────────────────────────────────────────────────────────

@Composable
fun PartnersTab(
    vehicleId: Long,
    shares: List<VehiclePartner>,
    allPartners: List<com.fleet.ledger.data.Partner>,
    vm: MainViewModel
) {
    var showAssign by remember { mutableStateOf(false) }

    if (showAssign) {
        AssignPartnerDialog(
            vehicleId = vehicleId,
            allPartners = allPartners,
            currentShares = shares,
            onDismiss = { showAssign = false },
            onAssign = { pid, share -> vm.setVehiclePartner(vehicleId, pid, share); showAssign = false }
        )
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (shares.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, null, modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                            Spacer(Modifier.height(8.dp))
                            Text("Bu araçta ortak yok",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            } else {
                val totalShare = shares.sumOf { it.sharePercent }
                item {
                    ProCard {
                        Text("Toplam Hisse: %${totalShare.toInt()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (totalShare == 100.0) Green500 else Amber500)
                    }
                }
                items(shares, key = { it.partnerId }) { share ->
                    val partner = allPartners.find { it.id == share.partnerId }
                    ProCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(partner?.name?.take(1)?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(partner?.name ?: "Bilinmiyor",
                                    style = MaterialTheme.typography.titleSmall)
                                if (partner?.phone?.isNotBlank() == true)
                                    Text(partner.phone, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusBadge("%${share.sharePercent.toInt()}", Blue500)
                            Spacer(Modifier.width(4.dp))
                            IconButton(onClick = { vm.removeVehiclePartner(vehicleId, share.partnerId) },
                                modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(onClick = { showAssign = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Default.PersonAdd, "Ortak Ata")
        }
    }
}

@Composable
fun AssignPartnerDialog(
    vehicleId: Long,
    allPartners: List<com.fleet.ledger.data.Partner>,
    currentShares: List<VehiclePartner>,
    onDismiss: () -> Unit,
    onAssign: (Long, Double) -> Unit
) {
    var selectedPartnerId by remember { mutableStateOf<Long?>(null) }
    var shareText by remember { mutableStateOf("") }

    val available = allPartners.filter { p -> currentShares.none { it.partnerId == p.id } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ortak Ata") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (available.isEmpty()) {
                    Text("Tüm ortaklar bu araca atanmış.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text("Ortak Seç", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    available.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedPartnerId = p.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedPartnerId == p.id,
                                onClick = { selectedPartnerId = p.id })
                            Spacer(Modifier.width(8.dp))
                            Text(p.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    OutlinedTextField(
                        value = shareText, onValueChange = { shareText = it },
                        label = { Text("Hisse Yüzdesi (0-100)") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        suffix = { Text("%") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pid = selectedPartnerId ?: return@Button
                    val share = shareText.toDoubleOrNull() ?: return@Button
                    onAssign(pid, share.coerceIn(0.0, 100.0))
                },
                enabled = selectedPartnerId != null && shareText.toDoubleOrNull() != null
            ) { Text("Ata") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
