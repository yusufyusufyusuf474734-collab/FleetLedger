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
import com.fleet.ledger.ui.*

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
                        bottomBar = {
                            NavigationBar(containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 0.dp) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 },
                                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                                    label = { Text("Özet") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 },
                                    icon = { Icon(Icons.Default.DirectionsBus, "Araçlar") },
                                    label = { Text("Araçlar") }
                                )
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(if (currentTab == 0) "Genel Bakış" else "Araçlar",
                                        style = MaterialTheme.typography.titleMedium)
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface)
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentWindowInsets = WindowInsets(0)
                    ) { padding ->
                        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                            when (currentTab) {
                                0 -> DashboardScreen(vm)
                                1 -> VehiclesScreen(vm, onVehicleClick = { selectedVehicleId = it })
                            }
                        }
                    }
                }
            }
        }
    }
}
