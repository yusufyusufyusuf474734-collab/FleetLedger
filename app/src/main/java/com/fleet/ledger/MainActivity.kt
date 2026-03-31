package com.fleet.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fleet.ledger.ui.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                var selectedVehicleId by remember { mutableStateOf<Long?>(null) }
                var currentTab by remember { mutableIntStateOf(0) }

                if (selectedVehicleId != null) {
                    VehicleDetailScreen(
                        vehicleId = selectedVehicleId!!,
                        vm = vm,
                        onBack = { selectedVehicleId = null }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        when (currentTab) {
                                            0 -> "Genel Bakış"
                                            1 -> "Araçlar"
                                            2 -> "Ortaklar"
                                            3 -> "Aylık Rapor"
                                            else -> "FiloTakip"
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface)
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0, onClick = { currentTab = 0 },
                                    icon = { Icon(Icons.Default.Dashboard, null) },
                                    label = { Text("Özet") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1, onClick = { currentTab = 1 },
                                    icon = { Icon(Icons.Default.DirectionsBus, null) },
                                    label = { Text("Araçlar") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == 2, onClick = { currentTab = 2 },
                                    icon = { Icon(Icons.Default.Group, null) },
                                    label = { Text("Ortaklar") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == 3, onClick = { currentTab = 3 },
                                    icon = { Icon(Icons.Default.BarChart, null) },
                                    label = { Text("Rapor") }
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentWindowInsets = WindowInsets(0)
                    ) { padding ->
                        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                            when (currentTab) {
                                0 -> DashboardScreen(vm)
                                1 -> VehiclesScreen(vm, onVehicleClick = { selectedVehicleId = it })
                                2 -> PartnersScreen(vm)
                                3 -> MonthlyReportScreen(vm)
                            }
                        }
                    }
                }
            }
        }
    }
}
