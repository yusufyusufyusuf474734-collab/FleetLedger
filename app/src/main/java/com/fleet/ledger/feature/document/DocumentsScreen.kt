package com.fleet.ledger.feature.document

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.core.domain.model.Document
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.ui.components.EmptyState
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.StatusBadge
import com.fleet.ledger.ui.theme.Error
import com.fleet.ledger.ui.theme.Success
import com.fleet.ledger.ui.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel,
    modifier: Modifier = Modifier
) {
    val documents by viewModel.documents.collectAsState()
    val expiringSoon by viewModel.expiringSoon.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Belgeler",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (expiringSoon.isNotEmpty()) {
            item {
                ExpiringDocumentsCard(documents = expiringSoon)
            }
        }

        if (documents.isEmpty()) {
            item {
                EmptyState(
                    message = "Henüz belge eklenmemiş",
                    icon = Icons.Default.Description
                )
            }
        } else {
            items(documents) { document ->
                DocumentCard(document = document)
            }
        }
    }
}

@Composable
private fun ExpiringDocumentsCard(
    documents: List<Document>,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Yaklaşan Son Tarihler (${documents.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Warning
                )
            }
            documents.take(3).forEach { doc ->
                Text(
                    text = "${doc.title} - ${doc.expiryDate?.formatDate() ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    modifier: Modifier = Modifier
) {
    val daysUntilExpiry = document.expiryDate?.let {
        ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    val statusColor = when {
        daysUntilExpiry == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysUntilExpiry < 0 -> Error
        daysUntilExpiry < 30 -> Warning
        else -> Success
    }

    EnterpriseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = document.type.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (daysUntilExpiry != null) {
                    StatusBadge(
                        text = when {
                            daysUntilExpiry < 0 -> "Süresi Doldu"
                            daysUntilExpiry < 30 -> "$daysUntilExpiry gün"
                            else -> "Geçerli"
                        },
                        color = statusColor
                    )
                }
            }

            if (document.company.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = document.company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (document.expiryDate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Son Tarih:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = document.expiryDate.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            if (document.amount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tutar:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = document.amount.formatCurrency(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
