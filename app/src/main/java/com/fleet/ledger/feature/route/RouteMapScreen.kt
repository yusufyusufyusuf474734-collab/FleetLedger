package com.fleet.ledger.feature.route

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
import com.fleet.ledger.core.util.formatCurrency
import com.fleet.ledger.core.util.formatDate
import com.fleet.ledger.ui.components.EnterpriseCard
import com.fleet.ledger.ui.components.Section

/**
 * Rota ve Harita Ekranı
 * Google Maps entegrasyonu ile sefer rotalarını gösterir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMapScreen(
    viewModel: RouteMapViewModel,
    modifier: Modifier = Modifier
) {
    val routes by viewModel.routes.collectAsState()
    val selectedRoute by viewModel.selectedRoute.collectAsState()
    var showMapView by remember { mutableStateOf(true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Yeni rota ekle */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Rota Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Harita/Liste Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rotalar",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showMapView = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (showMapView) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(Icons.Default.Map, contentDescription = "Harita")
                    }
                    IconButton(
                        onClick = { showMapView = false },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (!showMapView) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Liste")
                    }
                }
            }

            if (showMapView) {
                // TODO: Google Maps entegrasyonu
                MapViewPlaceholder()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(routes) { route ->
                        RouteCard(
                            route = route,
                            onClick = { viewModel.selectRoute(route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapViewPlaceholder(modifier: Modifier = Modifier) {
    EnterpriseCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(400.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Google Maps Entegrasyonu",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Rotalar burada gösterilecek",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RouteCard(
    route: Route,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnterpriseCard(
        modifier = modifier,
        onClick = onClick
    ) {
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
                        text = "${route.startLocation} → ${route.endLocation}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = route.date.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RouteInfo(
                    icon = Icons.Default.Route,
                    label = "Mesafe",
                    value = "${route.distanceKm} km"
                )
                RouteInfo(
                    icon = Icons.Default.Timer,
                    label = "Süre",
                    value = "${route.durationMinutes} dk"
                )
                RouteInfo(
                    icon = Icons.Default.AttachMoney,
                    label = "Gelir",
                    value = route.income.formatCurrency()
                )
            }
        }
    }
}

@Composable
private fun RouteInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class Route(
    val id: Long,
    val vehicleId: Long,
    val startLocation: String,
    val endLocation: String,
    val date: Long,
    val distanceKm: Double,
    val durationMinutes: Int,
    val income: Double,
    val coordinates: List<LatLng> = emptyList()
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)
