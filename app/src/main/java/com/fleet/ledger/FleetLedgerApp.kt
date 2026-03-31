package com.fleet.ledger

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.feature.dashboard.DashboardScreen
import com.fleet.ledger.feature.dashboard.DashboardViewModel
import com.fleet.ledger.ui.theme.FleetLedgerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetLedgerApp(
    dashboardViewModel: DashboardViewModel,
    vehiclesViewModel: com.fleet.ledger.feature.vehicle.VehiclesViewModel,
    partnersViewModel: com.fleet.ledger.feature.partner.PartnersViewModel,
    reportsViewModel: com.fleet.ledger.feature.report.ReportsViewModel,
    documentsViewModel: com.fleet.ledger.feature.document.DocumentsViewModel,
    settingsViewModel: com.fleet.ledger.feature.settings.SettingsViewModel
) {
    FleetLedgerTheme {
        var selectedTab by remember { mutableIntStateOf(0) }
        var selectedVehicleId by remember { mutableStateOf<Long?>(null) }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                0 -> "Genel Bakış"
                                1 -> "Araçlar"
                                2 -> "Ortaklar"
                                3 -> "Belgeler"
                                4 -> "Raporlar"
                                else -> "FiloTakip"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { selectedTab = 5 }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                if (selectedTab != 5) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            label = { Text("Özet") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
                            label = { Text("Araçlar") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.Group, contentDescription = null) },
                            label = { Text("Ortaklar") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.Description, contentDescription = null) },
                            label = { Text("Belgeler") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 },
                            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                            label = { Text("Raporlar") }
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> DashboardScreen(viewModel = dashboardViewModel)
                    1 -> com.fleet.ledger.feature.vehicle.VehiclesScreen(
                        viewModel = vehiclesViewModel,
                        onVehicleClick = { selectedVehicleId = it }
                    )
                    2 -> com.fleet.ledger.feature.partner.PartnersScreen(
                        viewModel = partnersViewModel
                    )
                    3 -> com.fleet.ledger.feature.document.DocumentsScreen(
                        viewModel = documentsViewModel
                    )
                    4 -> com.fleet.ledger.feature.report.ReportsScreen(
                        viewModel = reportsViewModel
                    )
                    5 -> com.fleet.ledger.feature.settings.SettingsScreen(
                        viewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "$title ekranı yakında eklenecek",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
