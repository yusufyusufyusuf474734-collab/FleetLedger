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
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(vm: MainViewModel) {
    val vehicles  by vm.vehicles.collectAsState()
    val summaries by vm.summaries.collectAsState()
    val expiring  by vm.expiringSoon.collectAsState()

    val totalIncome  = summaries.sumOf { it.totalIncome }
    val totalExpense = summaries.sumOf { it.totalExpense }
    val totalNet     = totalIncome - totalExpense

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // KPI kartları
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Toplam Gelir", totalIncome, Green500, Icons.Default.TrendingUp, Modifier.weight(1f))
                KpiCard("Toplam Gider", totalExpense, Red500, Icons.Default.TrendingDown, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Net Kâr", totalNet,
                    if (totalNet >= 0) Green500 else Red500,
                    Icons.Default.AccountBalance, Modifier.weight(1f))
                KpiCard("Aktif Araç", vehicles.size.toDouble(), Blue500,
                    Icons.Default.DirectionsBus, Modifier.weight(1f), isCount = true)
            }
        }

        // Yaklaşan belgeler
        if (expiring.isNotEmpty()) {
            item { SectionHeader("Belge Uyarıları") }
            item {
                ProCard {
                    expiring.take(5).forEach { doc ->
                        val vehicle = vm.vehicleById(doc.vehicleId)
                        val daysLeft = doc.expiryDate?.let {
                            TimeUnit.MILLISECONDS.toDays(it - System.currentTimeMillis())
                        } ?: 0
                        val color = if (daysLeft <= 7) Red500 else Amber500
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = color,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${vehicle?.plate ?: "?"} — ${doc.type.label}",
                                    style = MaterialTheme.typography.bodySmall)
                                if (doc.company.isNotBlank())
                                    Text(doc.company, style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusBadge(
                                if (daysLeft < 0) "Doldu" else "$daysLeft gün", color)
                        }
                        if (doc != expiring.take(5).last()) ProDivider()
                    }
                }
            }
        }

        // Araç özeti
        if (vehicles.isNotEmpty()) {
            item { SectionHeader("Araç Özeti") }
            items(vehicles, key = { it.id }) { vehicle ->
                val s = summaries.find { it.vehicleId == vehicle.id }
                ProCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DirectionsBus, null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(vehicle.plate, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Text(vehicle.name, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (s != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(s.netProfit.tl(), style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (s.netProfit >= 0) Green500 else Red500)
                                Text("Net", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    label: String, value: Double, color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    isCount: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.small,
                    color = color.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                if (isCount) value.toInt().toString() else value.tl(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
