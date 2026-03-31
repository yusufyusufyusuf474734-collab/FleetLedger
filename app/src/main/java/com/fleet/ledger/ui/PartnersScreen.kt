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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fleet.ledger.MainViewModel
import com.fleet.ledger.data.Partner

@Composable
fun PartnersScreen(vm: MainViewModel) {
    val partners by vm.allPartners.collectAsState()
    val vehicles by vm.vehicles.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editPartner by remember { mutableStateOf<Partner?>(null) }
    var deletePartner by remember { mutableStateOf<Partner?>(null) }

    if (showAdd || editPartner != null) {
        PartnerFormDialog(
            existing = editPartner,
            onDismiss = { showAdd = false; editPartner = null },
            onSave = { name, phone, note ->
                if (editPartner != null) vm.updatePartner(editPartner!!.copy(name = name, phone = phone, note = note))
                else vm.addPartner(name, phone, note)
                showAdd = false; editPartner = null
            }
        )
    }

    deletePartner?.let { p ->
        AlertDialog(
            onDismissRequest = { deletePartner = null },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Ortağı Sil") },
            text = { Text("${p.name} ortaktan çıkarılacak.") },
            confirmButton = {
                Button(onClick = { vm.deletePartner(p); deletePartner = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deletePartner = null }) { Text("İptal") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.PersonAdd, "Ortak Ekle")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (partners.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Group, null, modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                    Spacer(Modifier.height(12.dp))
                    Text("Ortak eklenmemiş",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(partners, key = { it.id }) { partner ->
                    ProCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(44.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(partner.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(partner.name, style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold)
                                if (partner.phone.isNotBlank())
                                    Text(partner.phone, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (partner.note.isNotBlank())
                                    Text(partner.note, style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                            IconButton(onClick = { editPartner = partner }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { deletePartner = partner }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerFormDialog(
    existing: Partner?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name  by remember { mutableStateOf(existing?.name ?: "") }
    var phone by remember { mutableStateOf(existing?.phone ?: "") }
    var note  by remember { mutableStateOf(existing?.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Ortak Düzenle" else "Ortak Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Ad Soyad *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it },
                    label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = note, onValueChange = { note = it },
                    label = { Text("Not") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name.trim(), phone.trim(), note.trim()) },
                enabled = name.isNotBlank()) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
