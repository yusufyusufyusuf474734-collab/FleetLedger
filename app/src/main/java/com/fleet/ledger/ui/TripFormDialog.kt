package com.fleet.ledger.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fleet.ledger.data.Trip
import java.io.File
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
    var receiptPath by remember { mutableStateOf(existing?.receiptImagePath ?: "") }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Dosyayı uygulama dizinine kopyala
            val dest = File(context.filesDir, "receipt_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                dest.outputStream().use { out -> input.copyTo(out) }
            }
            receiptPath = dest.absolutePath
        }
    }

    fun d(s: String) = s.toDoubleOrNull() ?: 0.0
    val net = d(income) - d(fuel) - d(bridge) - d(highway) - d(driverFee) - d(other)

    @Composable
    fun TF(label: String, value: String, isNum: Boolean = false, onChange: (String) -> Unit) {
        OutlinedTextField(
            value = value, onValueChange = onChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = if (isNum) KeyboardOptions(keyboardType = KeyboardType.Number)
                              else KeyboardOptions.Default
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Sefer Düzenle" else "Sefer Ekle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TF("Tarih (gg.aa.yyyy)", date) { date = it }
                TF("Sefer Açıklaması (örn: İst-Ank)", desc) { desc = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Text("Gelir", style = MaterialTheme.typography.labelMedium, color = Green500)
                TF("Gelir (₺)", income, isNum = true) { income = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Text("Giderler", style = MaterialTheme.typography.labelMedium, color = Red500)
                TF("Yakıt (₺)", fuel, isNum = true) { fuel = it }
                TF("Köprü (₺)", bridge, isNum = true) { bridge = it }
                TF("Otoban (₺)", highway, isNum = true) { highway = it }
                TF("Şoför Ücreti (₺)", driverFee, isNum = true) { driverFee = it }
                TF("Diğer Gider (₺)", other, isNum = true) { other = it }
                TF("Not", note) { note = it }
                // Fiş fotoğrafı
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Receipt, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (receiptPath.isNotBlank()) "Fiş eklendi ✓" else "Fiş fotoğrafı ekle",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (receiptPath.isNotBlank()) Green500
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Seç", style = MaterialTheme.typography.labelSmall)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (net >= 0) Green500.copy(alpha = 0.1f) else Red500.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                onSave(Trip(
                    id = existing?.id ?: 0,
                    vehicleId = vehicleId,
                    date = epoch,
                    description = desc.trim(),
                    income = d(income),
                    fuelCost = d(fuel),
                    bridgeCost = d(bridge),
                    highwayCost = d(highway),
                    driverFee = d(driverFee),
                    otherCost = d(other),
                    note = note.trim(),
                    receiptImagePath = receiptPath
                ))
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
