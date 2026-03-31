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
import com.fleet.ledger.data.VehicleSummary
import java.util.Calendar

@Composable
fun MonthlyReportScreen(vm: MainViewModel) {
    val now = Calendar.getInstance()
    var selectedYear  by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }

    val (from, to) = vm.monthRange(selectedYear, selectedMonth)
    val summaries by vm.summariesInRange(from, to).collectAsState(emptyList())
    val vehicles  by vm.vehicles.collectAsState()
    val allPartners by vm.allPartners.collectAsState()

    val totalIncome  = summaries.sumOf { it.totalIncome }
    val totalExpense = summaries.sumOf { it.totalExpense }
    val totalNet     = totalIncome - totalExpense

    val monthNames = listOf("Oca","Şub","Mar","Nis","May","Haz","Tem","Ağu","Eyl","Eki","Kas","Ara")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ay/Yıl seçici
        item {
            ProCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (selectedMonth == 1) { selectedMonth = 12; selectedYear-- }
                        else selectedMonth--
                    }) { Icon(Icons.Default.ChevronLeft, "Önceki") }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${monthNames[selectedMonth - 1]} $selectedYear",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Text("Aylık Rapor", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(onClick = {
                        if (selectedMonth == 12) { selectedMonth = 1; selectedYear++ }
                        else selectedMonth++
                    }) { Icon(Icons.Default.ChevronRight, "Sonraki") }
                }
            }
        }

        // Ay özeti KPI
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Gelir", totalIncome, Green500, Icons.Default.TrendingUp, Modifier.weight(1f))
                KpiCard("Gider", totalExpense, Red500, Icons.Default.TrendingDown, Modifier.weight(1f))
                KpiCard("Net", totalNet, if (totalNet >= 0) Green500 else Red500,
                    Icons.Default.AccountBalance, Modifier.weight(1f))
            }
        }

        // Araç bazlı detay
        if (summaries.isNotEmpty()) {
            item { SectionHeader("Araç Bazlı Detay") }
            items(summaries, key = { it.vehicleId }) { s ->
                val vehicle = vehicles.find { it.id == s.vehicleId } ?: return@items
                VehicleMonthCard(vehicle.plate, vehicle.name, s, vm, allPartners)
            }
        } else {
            item {
                ProCard {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            Spacer(Modifier.height(8.dp))
                            Text("Bu ay sefer kaydı yok",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleMonthCard(
    plate: String,
    name: String,
    summary: VehicleSummary,
    vm: MainViewModel,
    allPartners: List<com.fleet.ledger.data.Partner>
) {
    val shares by vm.sharesFor(summary.vehicleId).collectAsState(emptyList())

    ProCard {
        // Başlık
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBus, null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(plate, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(name, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(summary.netProfit.tl(),
                if (summary.netProfit >= 0) Green500 else Red500)
        }

        ProDivider()

        // Gelir/Gider satırları
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AmountCol("Gelir", summary.totalIncome, Green500)
            AmountCol("Gider", summary.totalExpense, Red500)
            AmountCol("Net Kâr", summary.netProfit, if (summary.netProfit >= 0) Green500 else Red500)
        }

        // Ortaklık paylaşımı
        if (shares.isNotEmpty()) {
            ProDivider()
            Text("Ortaklık Paylaşımı", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            shares.forEach { share ->
                val partner = allPartners.find { it.id == share.partnerId }
                val partnerNet = summary.netProfit * share.sharePercent / 100.0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(partner?.name?.take(1)?.uppercase() ?: "?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(partner?.name ?: "Bilinmiyor",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f))
                    Text("%${share.sharePercent.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Text(partnerNet.tl(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (partnerNet >= 0) Green500 else Red500)
                }
            }
        }
    }
}

@Composable
fun AmountCol(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(amount.tl(), style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
