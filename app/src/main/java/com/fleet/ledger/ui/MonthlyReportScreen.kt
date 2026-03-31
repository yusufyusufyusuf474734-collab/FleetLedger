package com.fleet.ledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.MonthlyExpense
import com.fleet.ledger.data.VehicleSummary
import com.fleet.ledger.pdf.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@Composable
fun MonthlyReportScreen(vm: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val now = Calendar.getInstance()
    var selectedYear  by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }
    var pdfGenerating by remember { mutableStateOf(false) }

    val (from, to) = vm.monthRange(selectedYear, selectedMonth)
    val summaries    by vm.summariesInRange(from, to).collectAsState(emptyList())
    val vehicles     by vm.vehicles.collectAsState()
    val allPartners  by vm.allPartners.collectAsState()
    val monthlyExps  by vm.monthlyExpensesByMonth(selectedYear, selectedMonth).collectAsState(emptyList())

    val totalIncome     = summaries.sumOf { it.totalIncome }
    val totalTripExp    = summaries.sumOf { it.totalExpense }
    val totalMonthlyExp = monthlyExps.sumOf { it.amount }
    val totalNet        = totalIncome - totalTripExp - totalMonthlyExp

    val monthNames = listOf("Oca","Şub","Mar","Nis","May","Haz","Tem","Ağu","Eyl","Eki","Kas","Ara")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ay seçici
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
                    }) { Icon(Icons.Default.ChevronLeft, null) }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${monthNames[selectedMonth - 1]} $selectedYear",
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Aylık Rapor", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(onClick = {
                        if (selectedMonth == 12) { selectedMonth = 1; selectedYear++ }
                        else selectedMonth++
                    }) { Icon(Icons.Default.ChevronRight, null) }
                }
            }
        }

        // KPI
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Gelir", totalIncome, Green500, Icons.Default.TrendingUp, Modifier.weight(1f))
                KpiCard("Sefer Gid.", totalTripExp, Red500, Icons.Default.TrendingDown, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Sabit Gid.", totalMonthlyExp, Amber500, Icons.Default.Receipt, Modifier.weight(1f))
                KpiCard("Net Kâr", totalNet, if (totalNet >= 0) Green500 else Red500,
                    Icons.Default.AccountBalance, Modifier.weight(1f))
            }
        }

        // PDF paylaş butonu
        item {
            Button(
                onClick = {
                    pdfGenerating = true
                    scope.launch {
                        val sharesMap = vehicles.associate { v ->
                            v.id to vm.sharesFor(v.id).let { flow ->
                                var result = emptyList<com.fleet.ledger.data.VehiclePartner>()
                                val job = launch { flow.collect { result = it } }
                                kotlinx.coroutines.delay(200)
                                job.cancel()
                                result
                            }
                        }
                        val file = withContext(Dispatchers.IO) {
                            PdfGenerator.generateMonthlyReport(
                                context, selectedYear, selectedMonth,
                                vehicles, summaries, monthlyExps,
                                allPartners, sharesMap
                            )
                        }
                        pdfGenerating = false
                        PdfGenerator.shareFile(context, file,
                            "${monthNames[selectedMonth - 1]} $selectedYear Raporu")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !pdfGenerating
            ) {
                if (pdfGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("PDF Oluşturuluyor...")
                } else {
                    Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("PDF Oluştur & Paylaş")
                }
            }
        }

        // Araç bazlı detay
        if (vehicles.isNotEmpty()) {
            item { SectionHeader("Araç Bazlı Detay") }
            items(vehicles, key = { it.id }) { vehicle ->
                val s = summaries.find { it.vehicleId == vehicle.id }
                val vExps = monthlyExps.filter { it.vehicleId == vehicle.id }
                VehicleMonthCard(
                    vehicleId = vehicle.id,
                    plate = vehicle.plate,
                    name = vehicle.name,
                    summary = s,
                    monthlyExpenses = vExps,
                    year = selectedYear,
                    month = selectedMonth,
                    vm = vm,
                    allPartners = allPartners
                )
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
    vehicleId: Long,
    plate: String,
    name: String,
    summary: VehicleSummary?,
    monthlyExpenses: List<MonthlyExpense>,
    year: Int,
    month: Int,
    vm: MainViewModel,
    allPartners: List<com.fleet.ledger.data.Partner>
) {
    val shares by vm.sharesFor(vehicleId).collectAsState(emptyList())
    var showAddExpense by remember { mutableStateOf(false) }
    var editExpense by remember { mutableStateOf<MonthlyExpense?>(null) }

    val tripNet    = summary?.netProfit ?: 0.0
    val expTotal   = monthlyExpenses.sumOf { it.amount }
    val finalNet   = tripNet - expTotal

    if (showAddExpense || editExpense != null) {
        MonthlyExpenseDialog(
            vehicleId = vehicleId,
            year = year,
            month = month,
            existing = editExpense,
            onDismiss = { showAddExpense = false; editExpense = null },
            onSave = { e ->
                if (editExpense != null) vm.updateMonthlyExpense(e)
                else vm.addMonthlyExpense(e)
                showAddExpense = false; editExpense = null
            }
        )
    }

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
            StatusBadge(finalNet.tl(), if (finalNet >= 0) Green500 else Red500)
        }

        ProDivider()

        // Gelir/Gider özeti
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AmountCol("Gelir", summary?.totalIncome ?: 0.0, Green500)
            AmountCol("Sefer Gid.", summary?.totalExpense ?: 0.0, Red500)
            AmountCol("Sabit Gid.", expTotal, Amber500)
            AmountCol("Net", finalNet, if (finalNet >= 0) Green500 else Red500)
        }

        // Sabit giderler listesi
        if (monthlyExpenses.isNotEmpty()) {
            ProDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sabit Giderler",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddExpense = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            monthlyExpenses.forEach { exp ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = MaterialTheme.shapes.extraSmall,
                        color = Amber500.copy(alpha = 0.1f),
                        modifier = Modifier.size(6.dp)) {}
                    Spacer(Modifier.width(8.dp))
                    Text(exp.label, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f))
                    Text(exp.amount.tl(), style = MaterialTheme.typography.labelMedium,
                        color = Amber500)
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = { editExpense = exp }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { vm.deleteMonthlyExpense(exp) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } else {
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = { showAddExpense = true },
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Sabit Gider Ekle (SGK, HGS...)",
                    style = MaterialTheme.typography.labelSmall)
            }
        }

        // Ortaklık payları
        if (shares.isNotEmpty()) {
            ProDivider()
            Text("Ortaklık Payları", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            shares.forEach { share ->
                val partner = allPartners.find { it.id == share.partnerId }
                val partnerNet = finalNet * share.sharePercent / 100.0
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

@Composable
fun MonthlyExpenseDialog(
    vehicleId: Long,
    year: Int,
    month: Int,
    existing: MonthlyExpense?,
    onDismiss: () -> Unit,
    onSave: (MonthlyExpense) -> Unit
) {
    var label  by remember { mutableStateOf(existing?.label ?: "") }
    var amount by remember { mutableStateOf(existing?.amount?.toInt()?.toString() ?: "") }
    var note   by remember { mutableStateOf(existing?.note ?: "") }

    // Hızlı preset'ler
    val presets = listOf("SGK", "HGS", "Bakım", "Sigorta Taksiti", "Kredi Taksiti", "Diğer")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Gider Düzenle" else "Sabit Gider Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Hızlı seçim
                Text("Hızlı Seçim", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.take(3).forEach { p ->
                        FilterChip(selected = label == p, onClick = { label = p },
                            label = { Text(p, style = MaterialTheme.typography.labelSmall) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.drop(3).forEach { p ->
                        FilterChip(selected = label == p, onClick = { label = p },
                            label = { Text(p, style = MaterialTheme.typography.labelSmall) })
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                OutlinedTextField(value = label, onValueChange = { label = it },
                    label = { Text("Gider Adı *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it },
                    label = { Text("Tutar (₺) *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = note, onValueChange = { note = it },
                    label = { Text("Not") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(MonthlyExpense(
                        id = existing?.id ?: 0,
                        vehicleId = vehicleId,
                        year = year,
                        month = month,
                        label = label.trim(),
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        note = note.trim()
                    ))
                },
                enabled = label.isNotBlank() && amount.toDoubleOrNull() != null
            ) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
