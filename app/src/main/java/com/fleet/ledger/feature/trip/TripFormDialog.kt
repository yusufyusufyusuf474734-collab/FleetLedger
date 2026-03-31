package com.fleet.ledger.feature.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fleet.ledger.core.domain.model.Trip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFormDialog(
    vehicleId: Long,
    trip: Trip? = null,
    onDismiss: () -> Unit,
    onSave: (Trip) -> Unit
) {
    var date by remember { mutableLongStateOf(trip?.date ?: System.currentTimeMillis()) }
    var description by remember { mutableStateOf(trip?.description ?: "") }
    var income by remember { mutableStateOf(trip?.income?.toString() ?: "") }
    var fuelCost by remember { mutableStateOf(trip?.fuelCost?.toString() ?: "") }
    var bridgeCost by remember { mutableStateOf(trip?.bridgeCost?.toString() ?: "") }
    var highwayCost by remember { mutableStateOf(trip?.highwayCost?.toString() ?: "") }
    var driverFee by remember { mutableStateOf(trip?.driverFee?.toString() ?: "") }
    var otherCost by remember { mutableStateOf(trip?.otherCost?.toString() ?: "") }
    var note by remember { mutableStateOf(trip?.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (trip == null) "Yeni Sefer" else "Sefer Düzenle") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama") },
                    placeholder = { Text("İstanbul - Ankara") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Tarih: ${SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(date))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text("GELİR", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = income,
                    onValueChange = { income = it },
                    label = { Text("Gelir (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("GİDERLER", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = fuelCost,
                    onValueChange = { fuelCost = it },
                    label = { Text("Yakıt (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bridgeCost,
                    onValueChange = { bridgeCost = it },
                    label = { Text("Köprü (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = highwayCost,
                    onValueChange = { highwayCost = it },
                    label = { Text("Otoyol (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = driverFee,
                    onValueChange = { driverFee = it },
                    label = { Text("Şoför Ücreti (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = otherCost,
                    onValueChange = { otherCost = it },
                    label = { Text("Diğer Giderler (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Not (Opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Trip(
                            id = trip?.id ?: 0,
                            vehicleId = vehicleId,
                            date = date,
                            description = description,
                            income = income.toDoubleOrNull() ?: 0.0,
                            fuelCost = fuelCost.toDoubleOrNull() ?: 0.0,
                            bridgeCost = bridgeCost.toDoubleOrNull() ?: 0.0,
                            highwayCost = highwayCost.toDoubleOrNull() ?: 0.0,
                            driverFee = driverFee.toDoubleOrNull() ?: 0.0,
                            otherCost = otherCost.toDoubleOrNull() ?: 0.0,
                            note = note
                        )
                    )
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
