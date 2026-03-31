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
    var date      by remember { mutableStateOf(existing?.let { sdf.format(Date(it.date)) } ?: sdf.format(Date())) }
    var desc      by remember { mutableStateOf(existing?.description ?: "") }
    var income    by remember { mutableStateOf(existing?.income?.toInt()?.toString() ?: "") }
    var fuel      by remember { mutableStateOf(existing?.fuelCost?.toInt()?.toString() ?: "") }
    var bridge    by remember { mutableStateOf(existing?.bridgeCost?.toInt()?.toString() ?: "") }
    var highway   by remember { mutableStateOf(existing?.highwayCost?.toInt()?.toString() ?: "") }
    var driverFee by remember { mutableStateOf(existing?.driverFee?.toInt()?.toString() ?: "") }
    var other     by remember { mutableStateOf(existing?.otherCost?.toInt()?.toString() ?: "") }
    var note      by remember { mutableStateOf(existing?.note ?: "") }

    fun d(s: String) = s.toDoubleOrNull() ?: 0.0
    val net = d(income) - d(fuel) - d(bridge) - d(highway) - d(driverFee) - d(other)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Sefer Düzenle" else "Sefer Ekle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field("Tarih (gg.aa.yyyy)", date, isDate = true) { date = it }
                Field("Sefer Açıklaması (örn: İst-Ank)", desc) { desc = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Text("Gelir", style = MaterialTheme.typography.labelMedium,
                    color = Green500)
                Field("Gelir (₺)", income, isNum = true) { income = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Text("Giderler", style = MaterialTheme.typography.labelMedium,
                    color = Red500)
                Field("Yakıt (₺)", fuel, isNum = true) { fuel = it }
                Field("Köprü (₺)", bridge, isNum = true) { bridge = it }
                Field("Otoban (₺)", highway, isNum = true) { highway = it }
                Field("Şoför Ücreti (₺)", driverFee, isNum = true) { driverFee = it }
                Field("Diğer Gider (₺)", other, isNum = true) { other = it }
                Field("Not", note) { note = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Surface(shape = MaterialTheme.shapes.small,
                    color = if (net >= 0) Green500.copy(alpha = 0.1f) else Red500.copy(alpha = 0.1f)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Kar/Zarar", style = MaterialTheme.typography.bodySmall)
                        Text(net.tl(), style = MaterialTheme.typography.bodyMedium,
                            color = if (net >= 0) Green500 else Red500)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val epoch = try { sdf.parse(date)?.time ?: System.currentTimeMillis() }
                            catch (e: Exception) { System.currentTimeMillis() }
                onSave(Trip(id = existing?.id ?: 0, vehicleId = vehicleId, date = epoch,
                    description = desc.trim(), income = d(income), fuelCost = d(fuel),
                    bridgeCost = d(bridge), highwayCost = d(highway), driverFee = d(driverFee),
                    otherCost = d(other), note = note.trim()))
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
