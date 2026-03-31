package com.fleet.ledger.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.ui.components.EnterpriseCard
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    val searchResults by viewModel.searchResults.collectAsState()
    val filters by viewModel.filters.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { 
                searchQuery = it
                viewModel.search(it)
            },
            onSearch = { viewModel.search(it) },
            active = false,
            onActiveChange = {},
            placeholder = { Text("Sefer, araç, açıklama ara...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtreler")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {}

        // Filters
        if (showFilters) {
            FilterSection(
                filters = filters,
                onFiltersChange = { viewModel.updateFilters(it) }
            )
        }

        // Results
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(searchResults) { trip ->
                SearchResultCard(trip = trip)
            }
        }
    }
}

@Composable
private fun FilterSection(
    filters: SearchFilters,
    onFiltersChange: (SearchFilters) -> Unit,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier.padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtreler",
                style = MaterialTheme.typography.titleMedium
            )

            // Date Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Date picker */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Başlangıç")
                }
                OutlinedButton(
                    onClick = { /* TODO: Date picker */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bitiş")
                }
            }

            // Amount Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filters.minAmount?.toString() ?: "",
                    onValueChange = { 
                        onFiltersChange(filters.copy(minAmount = it.toDoubleOrNull()))
                    },
                    label = { Text("Min Tutar") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = filters.maxAmount?.toString() ?: "",
                    onValueChange = { 
                        onFiltersChange(filters.copy(maxAmount = it.toDoubleOrNull()))
                    },
                    label = { Text("Max Tutar") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Filter Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filters.showIncome,
                    onClick = { onFiltersChange(filters.copy(showIncome = !filters.showIncome)) },
                    label = { Text("Gelir") }
                )
                FilterChip(
                    selected = filters.showExpense,
                    onClick = { onFiltersChange(filters.copy(showExpense = !filters.showExpense)) },
                    label = { Text("Gider") }
                )
                FilterChip(
                    selected = filters.showProfit,
                    onClick = { onFiltersChange(filters.copy(showProfit = !filters.showProfit)) },
                    label = { Text("Kar") }
                )
            }

            Button(
                onClick = { onFiltersChange(SearchFilters()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Filtreleri Temizle")
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.description.ifBlank { "Sefer" },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = trip.date.formatDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = trip.netProfit.formatCurrency(),
                style = MaterialTheme.typography.titleMedium,
                color = if (trip.netProfit >= 0) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}
