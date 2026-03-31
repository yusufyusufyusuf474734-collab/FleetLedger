package com.fleet.ledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fleet.ledger.data.Trip
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripFormDialog(
    vehicleId: Long,
    existing: Trip?,
    onDismiss: () -> Unit,
    onSave: (Trip) -> Unit
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    var date        by remember { mutableStateOf(existing?.let { sdf.format(Date(it.date)) } ?: sdf.format(Date())) }
    var desc        by remember { mutableStateOf(existing?.description ?: "") }
    var income      by remember { mutableStateOf(existing?.income?.toInt()?.toString() ?: "") }
    var fuel        by remember { mutableStateOf(existing?.fuelCost?.toInt()?.toString() ?: "") }
    var bridge      by remember { mutableStateOf(existing?.bridgeCost?.toInt()?.toString() ?: "") }
    var highway     by remember { mutableStateOf(existing?.highwayCost?.toInt()?.toString() ?: "") }
    var driverFee   by remember { mutableStateOf(existing?.driverFee?.toInt()?.toString() ?: "") }
    var other       by remember { mutableStateOf(existing?.otherCost?.toInt()?.toString() ?: "") }
    var note        by remember { mutableStateOf(existing?.note ?: "") }

    fun parseDate(s: String): Long = try { sdf.parse(s)?.time ?: System.currentTimeMillis() }
                                     catch (e: Exception) { System.currentTimeMillis() }
    fun d(s: String) = s.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Sefer Düzenle" else "Sefer Ekle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoneyField("Tarih (gg.aa.yyyy)", date, isDate = true) { date = it }
                MoneyField("Sefer Açıklaması (örn: İst-Ank)", desc, isDate = false) { desc = it }
                Divider()
                Text("Gelir", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                MoneyField("Gelir (₺)", income) { income = it }
                Divider()
                Text("Giderler", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error)
                MoneyField("Yakıt (₺)", fuel) { fuel = it }
                MoneyField("Köprü (₺)", bridge) { bridge = it }
                MoneyField("Otoban (₺)", highway) { highway = it }
                MoneyField("Şoför Ücreti (₺)", driverFee) { driverFee = it }
                MoneyField("Diğer Gider (₺)", other) { other = it }
                Divider()
                MoneyField("Not", note, isDate = false) { note = it }

                // Anlık net hesap
                val net = d(income) - d(fuel) - d(bridge) - d(highway) - d(driverFee) - d(other)
                Surface(shape = MaterialTheme.shapes.small,
                    color = if (net >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Kar/Zarar", style = MaterialTheme.typography.bodySmall)
                        Text(net.fmt(), style = MaterialTheme.typography.bodyMedium,
                            color = if (net >= 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val trip = Trip(
                    id = existing?.id ?: 0,
                    vehicleId = vehicleId,
                    date = parseDate(date),
                    description = desc.trim(),
                    income = d(income),
                    fuelCost = d(fuel),
                    bridgeCost = d(bridge),
                    highwayCost = d(highway),
                    driverFee = d(driverFee),
                    otherCost = d(other),
                    note = note.trim()
                )
                onSave(trip)
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
fun MoneyField(label: String, value: String, isDate: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = if (isDate) KeyboardOptions.Default
                          else KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
