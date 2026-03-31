package com.fleet.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.fleet.ledger.ui.AppTheme
import com.fleet.ledger.ui.DashboardScreen
import com.fleet.ledger.ui.VehicleDetailScreen

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                var selectedVehicleId by remember { mutableStateOf<Long?>(null) }
                if (selectedVehicleId == null) {
                    DashboardScreen(vm, onVehicleClick = { selectedVehicleId = it })
                } else {
                    VehicleDetailScreen(
                        vehicleId = selectedVehicleId!!,
                        vm = vm,
                        onBack = { selectedVehicleId = null }
                    )
                }
            }
        }
    }
}
