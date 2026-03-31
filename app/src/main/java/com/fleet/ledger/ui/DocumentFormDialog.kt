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
import com.fleet.ledger.data.Document
import com.fleet.ledger.data.DocumentType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DocumentFormDialog(
    vehicleId: Long,
    existing: Document?,
    onDismiss: () -> Unit,
    onSave: (Document) -> Unit
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    fun Long?.fmt() = this?.let { sdf.format(Date(it)) } ?: ""
    fun String.toEpoch(): Long? = try { sdf.parse(this)?.time } catch (e: Exception) { null }

    var type       by remember { mutableStateOf(existing?.type ?: DocumentType.KASKO) }
    var title      by remember { mutableStateOf(existing?.title ?: "") }
    var company    by remember { mutableStateOf(existing?.company ?: "") }
    var policyNo   by remember { mutableStateOf(existing?.policyNo ?: "") }
    var startDate  by remember { mutableStateOf(existing?.startDate.fmt()) }
    var expiryDate by remember { mutableStateOf(existing?.expiryDate.fmt()) }
    var amount     by remember { mutableStateOf(existing?.amount?.takeIf { it > 0 }?.toInt()?.toString() ?: "") }
    var note       by remember { mutableStateOf(existing?.note ?: "") }

    val allTypes = DocumentType.entries.toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Belge Düzenle" else "Belge Ekle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Belge Türü", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                // 3'lü satırlar
                val rows = allTypes.chunked(3)
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(t.label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Boş hücre dolgusu
                        val empty = 3 - row.size
                        if (empty > 0) {
                            repeat(empty) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                DocField("Başlık / Poliçe Adı", title) { title = it }
                DocField("Sigorta Şirketi", company) { company = it }
                DocField("Poliçe Numarası", policyNo) { policyNo = it }
                DocField("Başlangıç Tarihi (gg.aa.yyyy)", startDate) { startDate = it }
                DocField("Bitiş Tarihi (gg.aa.yyyy)", expiryDate) { expiryDate = it }
                DocField("Prim / Ücret (₺)", amount, isNum = true) { amount = it }
                DocField("Not", note) { note = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Document(
                    id = existing?.id ?: 0,
                    vehicleId = vehicleId,
                    type = type,
                    title = title.trim(),
                    company = company.trim(),
                    policyNo = policyNo.trim(),
                    startDate = startDate.toEpoch(),
                    expiryDate = expiryDate.toEpoch(),
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    note = note.trim()
                ))
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
fun DocField(label: String, value: String, isNum: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = if (isNum) KeyboardOptions(keyboardType = KeyboardType.Number)
                          else KeyboardOptions.Default
    )
}
