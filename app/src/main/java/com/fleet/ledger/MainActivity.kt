package com.fleet.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fleet.ledger.core.data.local.FleetDatabase
import com.fleet.ledger.core.data.repository.VehicleRepositoryImpl
import com.fleet.ledger.core.domain.usecase.GetVehiclesUseCase
import com.fleet.ledger.feature.dashboard.DashboardViewModel

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual DI - Production'da Hilt/Koin kullanılmalı
        val database = FleetDatabase.getInstance(applicationContext)
        val vehicleRepository = VehicleRepositoryImpl(database.vehicleDao())
        val getVehiclesUseCase = GetVehiclesUseCase(vehicleRepository)
        val dashboardViewModel = DashboardViewModel(getVehiclesUseCase)
        
        setContent {
            FleetLedgerApp(dashboardViewModel = dashboardViewModel)
        }
    }
}
