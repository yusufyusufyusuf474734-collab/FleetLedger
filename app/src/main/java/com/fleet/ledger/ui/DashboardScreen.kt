package com.fleet.ledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: MainViewModel) {
    val vehicles by vm.vehicles.collectAsState()
    val summaries by vm.summaries.collectAsState()
    val expiring by vm.expiringSoon.collectAsState()

    val totalIncome  = summaries.sumOf { it.totalIncome }
    val totalExpense = summaries.sumOf { it.totalExpense }
    val totalNet     = totalIncome - totalExpense

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Genel özet
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Genel Özet", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryTile("Toplam Gelir", totalIncome, Green500, Modifier.weight(1f))
                        SummaryTile("Toplam Gider", totalExpense, Red500, Modifier.weight(1f))
                        SummaryTile("Net Kar", totalNet,
                            if (totalNet >= 0) Green500 else Red500, Modifier.weight(1f))
                    }
                }
            }
        }

        // Yaklaşan belgeler uyarısı
        if (expiring.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Amber500.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Amber500,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Yaklaşan Belge Bitiş Tarihleri",
                                style = MaterialTheme.typography.titleSmall,
                                color = Amber500)
                        }
                        Spacer(Modifier.height(8.dp))
                        expiring.take(5).forEach { doc ->
                            val vehicle = vm.vehicleById(doc.vehicleId)
                            val daysLeft = doc.expiryDate?.let {
                                TimeUnit.MILLISECONDS.toDays(it - System.currentTimeMillis())
                            } ?: 0
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("${vehicle?.plate ?: "?"} — ${doc.type.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f))
                                Surface(shape = MaterialTheme.shapes.extraSmall,
                                    color = if (daysLeft <= 7) Red500.copy(alpha = 0.2f)
                                            else Amber500.copy(alpha = 0.2f)) {
                                    Text(
                                        if (daysLeft < 0) "Süresi doldu"
                                        else "$daysLeft gün kaldı",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (daysLeft <= 7) Red500 else Amber500,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Araç bazlı özet
        item {
            Text("Araçlar (${vehicles.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (vehicles.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(52.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                        Spacer(Modifier.height(10.dp))
                        Text("Araç eklenmemiş",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Text("Araçlar sekmesinden araç ekleyin",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                    }
                }
            }
        } else {
            items(vehicles, key = { it.id }) { vehicle ->
                val summary = summaries.find { it.vehicleId == vehicle.id }
                val net = summary?.netProfit ?: 0.0
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DirectionsBus, null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(vehicle.plate, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Text(vehicle.name, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (summary != null) {
                                Text("Gelir: ${summary.totalIncome.tl()}  ·  Gider: ${summary.totalExpense.tl()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(net.tl(), style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (net >= 0) Green500 else Red500)
                            Text("Net", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryTile(label: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small, color = color.copy(alpha = 0.1f), modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount.tl(), style = MaterialTheme.typography.labelLarge,
                color = color, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
